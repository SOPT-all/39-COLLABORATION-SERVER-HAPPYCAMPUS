package org.sopt.happycampus.folder.service;

import org.sopt.happycampus.folder.exception.FolderNotFoundException;
import org.sopt.happycampus.folder.repository.FolderItemRepository;
import org.sopt.happycampus.folder.repository.FolderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * 다른 도메인에서 폴더 연결(folder_items)을 다룰 때 사용하는 서비스.
 * 순환 의존을 막기 위해 FolderItemRepository에만 의존한다.
 */
@Service
@RequiredArgsConstructor
public class FolderItemService {
	private final FolderItemRepository folderItemRepository;
	private final FolderRepository folderRepository;

	// 장바구니 항목이 삭제될 때, 그 항목의 폴더 연결도 함께 삭제한다
	@Transactional
	public void deleteByCartItemId(Long cartItemId) {
		folderItemRepository.deleteByCartItemId(cartItemId);
	}

	public void validateFolderExists(Long folderId, Long userId) {
		if (!folderRepository.existsByIdAndUserId(folderId, userId)) {
			throw new FolderNotFoundException();
		};
	}
}
