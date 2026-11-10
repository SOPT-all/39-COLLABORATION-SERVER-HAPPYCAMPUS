package org.sopt.happycampus.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.cart.exception.CartItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/sql/cleanup.sql")
class CartItemServiceTest {
	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		jdbcTemplate.update("insert into users (id, created_at, updated_at) values (1, now(), now()), (2, now(), now())");
		jdbcTemplate.update("""
			insert into posts (id, category, title, created_at, updated_at)
			values (1, 'REPORT', '자료1', now(), now()), (2, 'REPORT', '자료2', now(), now())
			""");
		jdbcTemplate.update("""
			insert into cart_items (id, user_id, post_id, created_at)
			values (1, 1, 1, now()), (2, 1, 2, now())
			""");
		jdbcTemplate.update("insert into folders (id, user_id, name, created_at, updated_at) values (1, 1, '폴더', now(), now())");
		jdbcTemplate.update("insert into folder_items (id, folder_id, cart_item_id, created_at) values (1, 1, 1, now())");
	}

	private int count(String sql) {
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	@Test
	@DisplayName("폴더에 들어 있는 장바구니 자료를 삭제하면 폴더 연결도 함께 삭제되고 폴더는 남는다")
	void deleteCartItemInFolder() {
		cartItemService.deleteCartItem(1L, 1L);

		assertThat(count("select count(*) from cart_items where id = 1")).isZero();
		assertThat(count("select count(*) from folder_items where cart_item_id = 1")).isZero();
		assertThat(count("select count(*) from folders where id = 1")).isEqualTo(1);
	}

	@Test
	@DisplayName("폴더에 없는 장바구니 자료도 삭제된다")
	void deleteCartItemWithoutFolder() {
		cartItemService.deleteCartItem(1L, 2L);

		assertThat(count("select count(*) from cart_items where id = 2")).isZero();
	}

	@Test
	@DisplayName("사용자의 장바구니 항목을 ID 목록으로 조회하고, 중복 ID는 한 번만 조회한다")
	void getCartItems() {
		assertThat(cartItemService.getCartItems(1L, List.of(1L, 2L, 2L)))
			.extracting(CartItemEntity::getId)
			.containsExactlyInAnyOrder(1L, 2L);
	}

	@Test
	@DisplayName("없는 항목이나 다른 사용자의 항목이 하나라도 있으면 CartItemNotFoundException이 발생한다")
	void getCartItemsNotFound() {
		assertThatThrownBy(() -> cartItemService.getCartItems(1L, List.of(1L, 999L)))
			.isInstanceOf(CartItemNotFoundException.class);
		assertThatThrownBy(() -> cartItemService.getCartItems(2L, List.of(1L)))
			.isInstanceOf(CartItemNotFoundException.class);
	}

	@Test
	@DisplayName("다른 사용자의 장바구니 자료는 삭제할 수 없고 아무것도 지워지지 않는다")
	void deleteOtherUsersCartItem() {
		assertThatThrownBy(() -> cartItemService.deleteCartItem(2L, 1L))
			.isInstanceOf(CartItemNotFoundException.class);

		assertThat(count("select count(*) from cart_items where id = 1")).isEqualTo(1);
		assertThat(count("select count(*) from folder_items where cart_item_id = 1")).isEqualTo(1);
	}
}
