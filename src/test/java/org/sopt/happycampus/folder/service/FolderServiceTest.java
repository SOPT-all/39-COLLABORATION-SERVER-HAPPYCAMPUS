package org.sopt.happycampus.folder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.happycampus.folder.dto.request.CreateFolderRequest;
import org.sopt.happycampus.folder.dto.response.CreateFolderResponse;
import org.sopt.happycampus.folder.dto.response.FolderResponse;
import org.sopt.happycampus.folder.exception.FolderNameDuplicatedException;
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

	private void insertFolderItem(long id, long folderId, long cartItemId) {
		jdbcTemplate.update(
			"insert into folder_items (id, folder_id, cart_item_id, created_at) values (?, ?, ?, now())",
			id, folderId, cartItemId);
	}

	@Test
	@DisplayName("폴더 목록을 폴더별 자료 수와 함께 조회하고, 자료가 없는 폴더는 0개로 조회한다")
	void getFoldersWithItemCount() {
		insertFolder(1, 1, "자기소개서", "2026-01-01 00:00:00");
		insertFolder(2, 1, "빈폴더", "2026-01-02 00:00:00");
		insertFolderItem(1, 1, 1);
		insertFolderItem(2, 1, 2);

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
		insertFolderItem(1, 2, 3);

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
}
