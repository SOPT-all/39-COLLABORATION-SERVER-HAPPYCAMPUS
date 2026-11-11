package org.sopt.happycampus.folder.controller;

import java.util.List;

import org.sopt.happycampus.common.response.SuccessResponse;
import org.sopt.happycampus.folder.dto.request.CreateFolderRequest;
import org.sopt.happycampus.folder.dto.response.CreateFolderResponse;
import org.sopt.happycampus.folder.dto.response.FolderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Folder", description = "폴더 API")
public interface FolderControllerDocs {
	@Operation(summary = "폴더 목록 조회", description = "사용자의 폴더 목록을 폴더별 자료 수와 함께 생성순으로 조회합니다. 폴더가 없으면 빈 배열을 반환합니다.")
	ResponseEntity<SuccessResponse<List<FolderResponse>>> getFolders(
		@Parameter(description = "사용자 ID", example = "1") @PathVariable(name = "userId") Long userId);

	@Operation(summary = "폴더 생성", description = "새 폴더를 생성합니다. 이름은 공백 포함 8자 이하이며, 같은 사용자 안에서 중복될 수 없습니다(409).")
	ResponseEntity<SuccessResponse<CreateFolderResponse>> createFolder(
		@Parameter(description = "사용자 ID", example = "1") @PathVariable(name = "userId") Long userId,
		@Valid @RequestBody CreateFolderRequest request);
}
