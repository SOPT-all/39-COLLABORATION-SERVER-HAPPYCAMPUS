package org.sopt.happycampus.cart.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	// 사용자의 장바구니 항목을 ID 목록으로 조회한다. 하나라도 없거나 다른 사용자 소유면 예외가 발생한다.
	@Transactional(readOnly = true)
	public List<CartItemEntity> getCartItems(Long userId, Collection<Long> cartItemIds) {
		if (cartItemIds == null || cartItemIds.isEmpty()) {
			return List.of();
		}
		Set<Long> distinctIds = new HashSet<>(cartItemIds);
		List<CartItemEntity> cartItems = cartItemRepository.findAllByUserIdAndIdIn(userId, distinctIds);

		if (cartItems.size() != distinctIds.size()) {
			throw new CartItemNotFoundException();
		}
		return cartItems;
	}
}
