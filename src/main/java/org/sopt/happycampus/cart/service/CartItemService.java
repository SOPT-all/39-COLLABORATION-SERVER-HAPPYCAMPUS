package org.sopt.happycampus.cart.service;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.cart.exception.CartItemNotFoundException;
import org.sopt.happycampus.cart.repository.CartItemRepository;
import org.sopt.happycampus.folder.repository.FolderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartItemService {
	private final CartItemRepository cartItemRepository;
	private final FolderItemRepository folderItemRepository;

	public CartItemService(CartItemRepository cartItemRepository, FolderItemRepository folderItemRepository) {
		this.cartItemRepository = cartItemRepository;
		this.folderItemRepository = folderItemRepository;
	}

	@Transactional
	public void deleteCartItem(Long userId, Long cartItemId) {
		CartItemEntity cartItem =  cartItemRepository.findByIdAndUserId(cartItemId, userId).orElseThrow(CartItemNotFoundException::new);

		folderItemRepository.deleteByCartItemId(cartItemId);
		cartItemRepository.delete(cartItem);
	}
}
