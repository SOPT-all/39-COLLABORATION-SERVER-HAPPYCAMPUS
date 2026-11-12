package org.sopt.happycampus.cart.repository;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.post.entity.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartItemRepositoryCustom {
	Page<CartItemEntity> getCartItemsBySearchOption(Pageable pageable, Long userId, PostCategory category, Long folderId);
}
