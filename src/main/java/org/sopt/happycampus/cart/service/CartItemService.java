package org.sopt.happycampus.cart.service;

import org.sopt.happycampus.cart.dto.response.CartItemResponse;
import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.cart.exception.CartItemNotFoundException;
import org.sopt.happycampus.cart.repository.CartItemRepository;
import org.sopt.happycampus.folder.repository.FolderItemRepository;
import org.sopt.happycampus.folder.service.FolderService;
import org.sopt.happycampus.post.entity.PostCategory;
import org.sopt.happycampus.user.repository.UserRepository;
import org.sopt.happycampus.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {
	private final CartItemRepository cartItemRepository;
	private final FolderItemRepository folderItemRepository;
	private final UserService userService;
	private final FolderService folderService;

	@Transactional
	public void deleteCartItem(Long userId, Long cartItemId) {
		CartItemEntity cartItem =  cartItemRepository.findByIdAndUserId(cartItemId, userId).orElseThrow(CartItemNotFoundException::new);

		folderItemRepository.deleteByCartItemId(cartItemId);
		cartItemRepository.delete(cartItem);
	}

	@Transactional(readOnly = true)
	public Page<CartItemResponse> getCartItems(Long userId, Integer page, Integer size, PostCategory category, Long folderId) {
		userService.validateUserExists(userId);
		if (folderId != null) {
			folderService.validateFolderExists(folderId, userId);
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<CartItemEntity> cartItems = cartItemRepository.getCartItemsBySearchOption(pageable, userId, category, folderId);

		return cartItems.map(CartItemResponse::from);
	}
}
