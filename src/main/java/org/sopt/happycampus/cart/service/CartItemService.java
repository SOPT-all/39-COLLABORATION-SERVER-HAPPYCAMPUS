package org.sopt.happycampus.cart.service;

import org.sopt.happycampus.cart.dto.response.CartItemResponse;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.cart.exception.CartItemDuplicatedException;
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
import org.sopt.happycampus.post.entity.PostEntity;
import org.sopt.happycampus.post.service.PostService;
import org.sopt.happycampus.user.entity.UserEntity;
import org.sopt.happycampus.user.service.UserService;
import org.sopt.happycampus.folder.service.FolderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {
	private final CartItemRepository cartItemRepository;
	private final UserService userService;
	private final FolderItemService folderItemService;
	private final PostService postService;

	@Transactional
	public void deleteCartItem(Long userId, Long cartItemId) {
		CartItemEntity cartItem =  cartItemRepository.findByIdAndUserId(cartItemId, userId).orElseThrow(CartItemNotFoundException::new);

		folderItemService.deleteByCartItemId(cartItemId);
		cartItemRepository.delete(cartItem);
	}

	@Transactional(readOnly = true)
	public Page<CartItemResponse> getCartItems(Long userId, Integer page, Integer size, PostCategory category, Long folderId) {
		userService.validateUserExists(userId);
		if (folderId != null) {
			folderItemService.validateFolderExists(folderId, userId);
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<CartItemEntity> cartItems = cartItemRepository.getCartItemsBySearchOption(pageable, userId, category, folderId);

		return cartItems.map(CartItemResponse::from);
  }
	/**
	 * 사용자 장바구니에 자료를 추가한다.
	 *
	 * 사용자 확인 → 자료 확인 → 중복 확인 → 항목 생성 → DB 저장
	 * @param userId 사용자 ID
	 * @param postId 추가할 자료 ID
	 * @return 저장된 장바구니 항목
	 * @throws CartItemDuplicatedException 이미 담긴 자료인 경우
	 */
	@Transactional
	public CartItemEntity addCartItem(Long userId, Long postId) {
		UserEntity user = userService.getUser(userId);
		PostEntity post = postService.findPostById(postId);

		if (cartItemRepository.existsByUserIdAndPostId(userId, postId)) {
			throw new CartItemDuplicatedException();
		}

		CartItemEntity cartItem = CartItemEntity.create(user, post);

		try {
			return cartItemRepository.saveAndFlush(cartItem);
		} catch (DataIntegrityViolationException e) {
			if (isCartItemDuplicate(e)) {
				throw new CartItemDuplicatedException();
			}
			throw e;
		}
	}

	/**
	 * 예외 원인을 확인해 장바구니 중복 제약 위반인지 판단
	 *
	 * @param exception 저장 중 발생한 예외
	 * @return 장바구니 중복 제약 위반이면 true
	 */
	private boolean isCartItemDuplicate(Throwable exception) {
		Throwable cause = exception;

		while (cause != null) {
			if (cause instanceof ConstraintViolationException violation) {
				String name = violation.getConstraintName();

				return name != null
						&& (name.equals("uk_cart_items_user_post")
						|| name.endsWith(".uk_cart_items_user_post"));
			}

			cause = cause.getCause();
		}

		return false;
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
