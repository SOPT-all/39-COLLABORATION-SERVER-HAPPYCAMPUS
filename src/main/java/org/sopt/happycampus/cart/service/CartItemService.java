package org.sopt.happycampus.cart.service;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.cart.exception.CartItemNotFoundException;
import org.sopt.happycampus.cart.repository.CartItemRepository;
import org.sopt.happycampus.folder.service.FolderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {
	private final CartItemRepository cartItemRepository;
	private final FolderItemService folderItemService;

	@Transactional
	public void deleteCartItem(Long userId, Long cartItemId) {
		CartItemEntity cartItem =  cartItemRepository.findByIdAndUserId(cartItemId, userId).orElseThrow(CartItemNotFoundException::new);

		folderItemService.deleteByCartItemId(cartItemId);
		cartItemRepository.delete(cartItem);
	}
}
