package org.sopt.happycampus.folder.controller;

import java.util.List;

import org.sopt.happycampus.common.response.SuccessResponse;
import org.sopt.happycampus.folder.dto.response.FolderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Folder", description = "폴더 API")
public interface FolderControllerDocs {
	@Operation(summary = "폴더 목록 조회", description = "사용자의 폴더 목록을 폴더별 자료 수와 함께 생성순으로 조회합니다. 폴더가 없으면 빈 배열을 반환합니다.")
	ResponseEntity<SuccessResponse<List<FolderResponse>>> getFolders(
		@Parameter(description = "사용자 ID", example = "1") @PathVariable(name = "userId") Long userId);
}
