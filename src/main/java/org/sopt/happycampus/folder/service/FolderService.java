package org.sopt.happycampus.folder.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.cart.service.CartItemService;
import org.sopt.happycampus.folder.dto.request.AddCartItemsToFolderRequest;
import org.sopt.happycampus.folder.dto.request.CreateFolderRequest;
import org.sopt.happycampus.folder.dto.response.CreateFolderResponse;
import org.sopt.happycampus.folder.dto.response.FolderResponse;
import org.sopt.happycampus.folder.entity.FolderEntity;
import org.sopt.happycampus.folder.entity.FolderItemEntity;
import org.sopt.happycampus.folder.exception.FolderItemConflictException;
import org.sopt.happycampus.folder.exception.FolderNameDuplicatedException;
import org.sopt.happycampus.folder.exception.FolderNotFoundException;
import org.sopt.happycampus.folder.repository.FolderItemRepository;
import org.sopt.happycampus.folder.repository.FolderRepository;
import org.sopt.happycampus.user.entity.UserEntity;
import org.sopt.happycampus.user.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
	private final FolderRepository folderRepository;
	private final FolderItemRepository folderItemRepository;
	private final UserService userService;
	private final CartItemService cartItemService;

	@Transactional(readOnly = true)
	public List<FolderResponse> getFolders(Long userId) {
		return folderRepository.findAllWithItemCountByUserId(userId);
	}

	@Transactional
	public CreateFolderResponse createFolder(Long userId, CreateFolderRequest request) {
		UserEntity user = userService.getUser(userId);

		if (folderRepository.existsByUserIdAndName(userId, request.name())) {
			throw new FolderNameDuplicatedException();
		}

		try {
			FolderEntity folder = folderRepository.saveAndFlush(FolderEntity.create(user, request.name()));
			return CreateFolderResponse.from(folder);
		} catch (DataIntegrityViolationException e) {
			// 동시에 같은 이름으로 생성 요청이 들어와 유니크 제약에 걸린 경우
			throw new FolderNameDuplicatedException();
		}
	}

	public void validateFolderExists(Long folderId, Long userId) {
		if (!folderRepository.existsByIdAndUserId(folderId, userId)) {
			throw new FolderNotFoundException();
		};
  }
	/**
	 * 장바구니 항목들을 폴더에 넣는다.
	 * 한 항목은 여러 폴더에 담을 수 있고, 다른 폴더에 있던 연결은 그대로 둔다. 이미 이 폴더에 있는 항목은 건너뛴다.
	 * 폴더나 장바구니 항목 중 하나라도 없거나 다른 사용자 소유면 아무것도 바꾸지 않고 404.
	 * 같은 항목을 동시에 처리하는 요청(더블클릭 등)과 겹쳐 DB 제약에 걸리면 500 대신 409.
	 */
	@Transactional
	public void addCartItemsToFolder(Long userId, Long folderId, AddCartItemsToFolderRequest request) {
		FolderEntity folder = folderRepository.findByIdAndUserId(folderId, userId)
			.orElseThrow(FolderNotFoundException::new);
		List<CartItemEntity> cartItems = cartItemService.getCartItems(userId, request.cartItemIds());

		Set<Long> alreadyInFolder = folderItemRepository
			.findAllByFolderIdAndCartItemIdIn(folderId, cartItems.stream().map(CartItemEntity::getId).toList())
			.stream()
			.map(folderItem -> folderItem.getCartItem().getId())
			.collect(Collectors.toSet());

		List<FolderItemEntity> newFolderItems = cartItems.stream()
			.filter(cartItem -> !alreadyInFolder.contains(cartItem.getId()))
			.map(cartItem -> FolderItemEntity.create(folder, cartItem))
			.toList();
		try {
			folderItemRepository.saveAllAndFlush(newFolderItems);
		} catch (DataIntegrityViolationException e) {
			// 동시 요청이 먼저 같은 폴더에 같은 항목을 연결했거나(UNIQUE), 그 사이 항목이 삭제된 경우(FK)
			throw new FolderItemConflictException();
		}
	}
}
