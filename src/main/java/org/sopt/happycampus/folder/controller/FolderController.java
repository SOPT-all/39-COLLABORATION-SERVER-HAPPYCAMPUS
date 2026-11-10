package org.sopt.happycampus.folder.controller;

import java.util.List;

import org.sopt.happycampus.common.response.ApiResponse;
import org.sopt.happycampus.common.response.SuccessResponse;
import org.sopt.happycampus.folder.code.SuccessCode;
import org.sopt.happycampus.folder.dto.response.FolderResponse;
import org.sopt.happycampus.folder.service.FolderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class FolderController implements FolderControllerDocs {
	private final FolderService folderService;

	@GetMapping(path = "/users/{userId}/folders")
	public ResponseEntity<SuccessResponse<List<FolderResponse>>> getFolders(
		@PathVariable(name = "userId") Long userId) {

		List<FolderResponse> folders = folderService.getFolders(userId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.FOLDERS_FOUND, folders));
	}
}
