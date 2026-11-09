package org.sopt.happycampus.cart.service;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.cart.exception.CartItemNotFoundException;
import org.sopt.happycampus.cart.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartItemService {
	private final CartItemRepository cartItemRepository;

	public CartItemService(CartItemRepository cartItemRepository) {
		this.cartItemRepository = cartItemRepository;
	}

	@Transactional
	public void deleteCartItem(Long userId, Long cartItemId) {
		CartItemEntity cartItem =  cartItemRepository.findByIdAndUserId(cartItemId, userId).orElseThrow(CartItemNotFoundException::new);
		cartItemRepository.delete(cartItem);
	}
}
