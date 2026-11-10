package org.sopt.happycampus.folder.service;

import java.util.List;

import org.sopt.happycampus.folder.dto.response.FolderResponse;
import org.sopt.happycampus.folder.repository.FolderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
	private final FolderRepository folderRepository;

	@Transactional(readOnly = true)
	public List<FolderResponse> getFolders(Long userId) {
		return folderRepository.findAllWithItemCountByUserId(userId);
	}
}
