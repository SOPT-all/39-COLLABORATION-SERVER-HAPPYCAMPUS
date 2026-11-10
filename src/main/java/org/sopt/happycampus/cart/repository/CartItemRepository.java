package org.sopt.happycampus.cart.repository;

import java.util.Optional;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
	Optional<CartItemEntity> findByIdAndUserId(Long cartItemId, Long userId);
}
