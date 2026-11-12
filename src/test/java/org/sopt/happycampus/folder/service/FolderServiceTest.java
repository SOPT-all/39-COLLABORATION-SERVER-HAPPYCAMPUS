package org.sopt.happycampus.folder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.happycampus.cart.exception.CartItemNotFoundException;
import org.sopt.happycampus.folder.dto.request.AddCartItemsToFolderRequest;
import org.sopt.happycampus.folder.dto.request.CreateFolderRequest;
import org.sopt.happycampus.folder.dto.response.CreateFolderResponse;
import org.sopt.happycampus.folder.dto.response.FolderResponse;
import org.sopt.happycampus.folder.exception.FolderNameDuplicatedException;
import org.sopt.happycampus.folder.exception.FolderNotFoundException;
import org.sopt.happycampus.user.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/sql/cleanup.sql")
class FolderServiceTest {
	@Autowired
	private FolderService folderService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		jdbcTemplate.update("insert into users (id, created_at, updated_at) values (1, now(), now()), (2, now(), now())");
		jdbcTemplate.update("""
			insert into posts (id, category, title, created_at, updated_at)
			values (1, 'REPORT', '자료1', now(), now()), (2, 'REPORT', '자료2', now(), now()), (3, 'REPORT', '자료3', now(), now())
			""");
		jdbcTemplate.update("""
			insert into cart_items (id, user_id, post_id, created_at)
			values (1, 1, 1, now()), (2, 1, 2, now()), (3, 2, 3, now())
			""");
	}

	private void insertFolder(long id, long userId, String name, String createdAt) {
		jdbcTemplate.update(
			"insert into folders (id, user_id, name, created_at, updated_at) values (?, ?, ?, ?, ?)",
			id, userId, name, createdAt, createdAt);
	}

	// folder_items는 서비스가 새 행을 만드는 테이블이라 ID를 직접 넣지 않는다 (H2 자동 증가 번호와 충돌 방지)
	private void insertFolderItem(long folderId, long cartItemId) {
		jdbcTemplate.update(
			"insert into folder_items (folder_id, cart_item_id, created_at) values (?, ?, now())",
			folderId, cartItemId);
	}

	@Test
	@DisplayName("폴더 목록을 폴더별 자료 수와 함께 조회하고, 자료가 없는 폴더는 0개로 조회한다")
	void getFoldersWithItemCount() {
		insertFolder(1, 1, "자기소개서", "2026-01-01 00:00:00");
		insertFolder(2, 1, "빈폴더", "2026-01-02 00:00:00");
		insertFolderItem(1, 1);
		insertFolderItem(1, 2);

		List<FolderResponse> folders = folderService.getFolders(1L);

		assertThat(folders).containsExactly(
			new FolderResponse(1L, "자기소개서", 2L),
			new FolderResponse(2L, "빈폴더", 0L));
	}

	@Test
	@DisplayName("폴더 목록은 생성순으로 정렬된다")
	void getFoldersOrderByCreatedAt() {
		insertFolder(1, 1, "나중폴더", "2026-01-03 00:00:00");
		insertFolder(2, 1, "먼저폴더", "2026-01-01 00:00:00");

		List<FolderResponse> folders = folderService.getFolders(1L);

		assertThat(folders).extracting(FolderResponse::name)
			.containsExactly("먼저폴더", "나중폴더");
	}

	@Test
	@DisplayName("다른 사용자의 폴더는 조회되지 않는다")
	void getFoldersOnlyOwn() {
		insertFolder(1, 1, "내폴더", "2026-01-01 00:00:00");
		insertFolder(2, 2, "남의폴더", "2026-01-01 00:00:00");
		insertFolderItem(2, 3);

		List<FolderResponse> folders = folderService.getFolders(1L);

		assertThat(folders).containsExactly(new FolderResponse(1L, "내폴더", 0L));
	}

	@Test
	@DisplayName("폴더가 없으면 빈 목록을 반환한다")
	void getFoldersEmpty() {
		assertThat(folderService.getFolders(1L)).isEmpty();
		assertThat(folderService.getFolders(999L)).isEmpty();
	}

	private int countFolders(long userId, String name) {
		return jdbcTemplate.queryForObject(
			"select count(*) from folders where user_id = ? and name = ?", Integer.class, userId, name);
	}

	@Test
	@DisplayName("폴더를 생성하면 저장되고 생성된 폴더 ID와 이름을 반환한다")
	void createFolder() {
		CreateFolderResponse response = folderService.createFolder(1L, new CreateFolderRequest("자기소개서"));

		assertThat(response.folderId()).isNotNull();
		assertThat(response.name()).isEqualTo("자기소개서");
		assertThat(folderService.getFolders(1L))
			.containsExactly(new FolderResponse(response.folderId(), "자기소개서", 0L));
	}

	@Test
	@DisplayName("같은 사용자가 같은 이름의 폴더를 만들면 FolderNameDuplicatedException이 발생하고 저장되지 않는다")
	void createFolderDuplicatedName() {
		folderService.createFolder(1L, new CreateFolderRequest("자기소개서"));

		assertThatThrownBy(() -> folderService.createFolder(1L, new CreateFolderRequest("자기소개서")))
			.isInstanceOf(FolderNameDuplicatedException.class);
		assertThat(countFolders(1, "자기소개서")).isEqualTo(1);
	}

	@Test
	@DisplayName("앞뒤 공백만 다른 이름은 같은 이름으로 보고 FolderNameDuplicatedException이 발생한다")
	void createFolderDuplicatedNameWithSpaces() {
		CreateFolderResponse response = folderService.createFolder(1L, new CreateFolderRequest("  자기소개서  "));

		assertThat(response.name()).isEqualTo("자기소개서");
		assertThatThrownBy(() -> folderService.createFolder(1L, new CreateFolderRequest("자기소개서 ")))
			.isInstanceOf(FolderNameDuplicatedException.class);
		assertThat(countFolders(1, "자기소개서")).isEqualTo(1);
	}

	@Test
	@DisplayName("다른 사용자는 같은 이름의 폴더를 만들 수 있다")
	void createFolderSameNameOtherUser() {
		folderService.createFolder(1L, new CreateFolderRequest("자기소개서"));
		folderService.createFolder(2L, new CreateFolderRequest("자기소개서"));

		assertThat(countFolders(1, "자기소개서")).isEqualTo(1);
		assertThat(countFolders(2, "자기소개서")).isEqualTo(1);
	}

	@Test
	@DisplayName("존재하지 않는 사용자가 폴더를 만들면 UserNotFoundException이 발생한다")
	void createFolderUserNotFound() {
		assertThatThrownBy(() -> folderService.createFolder(999L, new CreateFolderRequest("자기소개서")))
			.isInstanceOf(UserNotFoundException.class);
		assertThat(jdbcTemplate.queryForObject("select count(*) from folders", Integer.class)).isZero();
	}

	private List<Long> folderIdsOf(long cartItemId) {
		return jdbcTemplate.queryForList(
			"select folder_id from folder_items where cart_item_id = ? order by folder_id", Long.class, cartItemId);
	}

	private void addCartItems(long userId, long folderId, Long... cartItemIds) {
		folderService.addCartItemsToFolder(userId, folderId, new AddCartItemsToFolderRequest(List.of(cartItemIds)));
	}

	@Test
	@DisplayName("폴더에 없던 장바구니 항목을 추가하고, 중복 ID는 한 번만 추가한다")
	void addCartItemsToFolder() {
		insertFolder(1, 1, "A", "2026-01-01 00:00:00");

		addCartItems(1, 1, 1L, 2L, 2L);

		assertThat(folderIdsOf(1)).containsExactly(1L);
		assertThat(folderIdsOf(2)).containsExactly(1L);
		assertThat(folderService.getFolders(1L)).containsExactly(new FolderResponse(1L, "A", 2L));
	}

	@Test
	@DisplayName("이미 다른 폴더에 있는 항목을 추가하면 기존 폴더에도 남고 요청한 폴더에도 담긴다")
	void addCartItemToAnotherFolderKeepsExisting() {
		insertFolder(1, 1, "A", "2026-01-01 00:00:00");
		insertFolder(2, 1, "B", "2026-01-02 00:00:00");
		insertFolderItem(1, 1);

		addCartItems(1, 2, 1L);

		assertThat(folderIdsOf(1)).containsExactly(1L, 2L);
	}

	@Test
	@DisplayName("한 요청에서 새 항목과 다른 폴더에 있던 항목을 함께 담으면 둘 다 요청한 폴더에 추가된다")
	void addNewAndExistingInOneRequest() {
		insertFolder(1, 1, "A", "2026-01-01 00:00:00");
		insertFolder(2, 1, "B", "2026-01-02 00:00:00");
		insertFolderItem(1, 1);

		addCartItems(1, 2, 1L, 2L);

		assertThat(folderIdsOf(1)).containsExactly(1L, 2L);
		assertThat(folderIdsOf(2)).containsExactly(2L);
		assertThat(folderService.getFolders(1L)).containsExactly(
			new FolderResponse(1L, "A", 1L),
			new FolderResponse(2L, "B", 2L));
	}

	@Test
	@DisplayName("이미 같은 폴더에 있는 항목을 다시 추가해도 그대로 하나만 있다")
	void addCartItemAlreadyInSameFolder() {
		insertFolder(1, 1, "A", "2026-01-01 00:00:00");
		insertFolderItem(1, 1);

		addCartItems(1, 1, 1L);

		assertThat(folderService.getFolders(1L)).containsExactly(new FolderResponse(1L, "A", 1L));
	}

	@Test
	@DisplayName("없는 폴더나 다른 사용자의 폴더면 FolderNotFoundException이 발생한다")
	void addCartItemsFolderNotFound() {
		insertFolder(1, 2, "남의폴더", "2026-01-01 00:00:00");

		assertThatThrownBy(() -> addCartItems(1, 1, 1L))
			.isInstanceOf(FolderNotFoundException.class);
		assertThatThrownBy(() -> addCartItems(1, 999, 1L))
			.isInstanceOf(FolderNotFoundException.class);
		assertThat(folderIdsOf(1)).isEmpty();
	}

	@Test
	@DisplayName("다른 사용자의 장바구니 항목이면 CartItemNotFoundException이 발생하고 아무것도 추가되지 않는다")
	void addOtherUsersCartItem() {
		insertFolder(1, 1, "A", "2026-01-01 00:00:00");

		assertThatThrownBy(() -> addCartItems(1, 1, 1L, 3L))
			.isInstanceOf(CartItemNotFoundException.class);
		assertThat(folderIdsOf(1)).isEmpty();
		assertThat(folderIdsOf(3)).isEmpty();
	}

	@Test
	@DisplayName("요청 중 하나라도 없는 항목이 있으면 아무 폴더에도 추가되지 않는다")
	void addCartItemsPartiallyMissing() {
		insertFolder(1, 1, "A", "2026-01-01 00:00:00");
		insertFolder(2, 1, "B", "2026-01-02 00:00:00");
		insertFolderItem(1, 1);

		assertThatThrownBy(() -> addCartItems(1, 2, 1L, 999L))
			.isInstanceOf(CartItemNotFoundException.class);
		assertThat(folderIdsOf(1)).containsExactly(1L);
	}
}
