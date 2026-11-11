package org.sopt.happycampus.folder.service;

import java.util.List;

import org.sopt.happycampus.folder.dto.request.CreateFolderRequest;
import org.sopt.happycampus.folder.dto.response.CreateFolderResponse;
import org.sopt.happycampus.folder.dto.response.FolderResponse;
import org.sopt.happycampus.folder.entity.FolderEntity;
import org.sopt.happycampus.folder.exception.FolderNameDuplicatedException;
import org.sopt.happycampus.folder.exception.FolderNotFoundException;
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
	private final UserService userService;

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
}
