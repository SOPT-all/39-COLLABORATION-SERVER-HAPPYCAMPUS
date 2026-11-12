package org.sopt.happycampus.folder.controller;

import java.util.List;

import org.sopt.happycampus.common.response.SuccessResponse;
import org.sopt.happycampus.folder.dto.request.AddCartItemsToFolderRequest;
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

	@Operation(summary = "폴더에 자료 추가", description = "장바구니 항목들을 폴더에 넣습니다. 한 항목은 여러 폴더에 담을 수 있고, 이미 이 폴더에 있는 항목은 건너뜁니다. "
		+ "폴더나 장바구니 항목 중 하나라도 없거나 다른 사용자 소유면 아무것도 바뀌지 않고 404를 반환합니다.")
	ResponseEntity<SuccessResponse<Void>> addCartItemsToFolder(
		@Parameter(description = "사용자 ID", example = "1") @PathVariable(name = "userId") Long userId,
		@Parameter(description = "폴더 ID", example = "1") @PathVariable(name = "folderId") Long folderId,
		@Valid @RequestBody AddCartItemsToFolderRequest request);
}
