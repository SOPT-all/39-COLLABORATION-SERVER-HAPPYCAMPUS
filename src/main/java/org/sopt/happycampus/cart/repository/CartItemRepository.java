package org.sopt.happycampus.cart.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.post.service.PostService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long>, CartItemRepositoryCustom {
	Optional<CartItemEntity> findByIdAndUserId(Long cartItemId, Long userId);

	/**
	 * 사용자가 해당 자료를 장바구니에 이미 담았는지 확인
	 *
	 * @param userId 사용자 ID
	 * @param postId 자료 ID
	 * @return 이미 담긴 자료이면 true
	 */
	boolean existsByUserIdAndPostId(Long userId, Long postId);

	/** 조회 사용자의 장바구니에서 해당 자료의 항목을 찾는다. */
	Optional<CartItemEntity> findByUserIdAndPostId(Long userId, Long postId);
  
	List<CartItemEntity> findAllByUserIdAndIdIn(Long userId, Collection<Long> cartItemIds);

	boolean existsByIdAndUserId(Long cartItemId, Long userId);
}
