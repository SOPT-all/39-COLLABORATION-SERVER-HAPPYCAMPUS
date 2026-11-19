package org.sopt.happycampus.post.controller;

import org.sopt.happycampus.common.response.SuccessResponse;
import org.sopt.happycampus.post.dto.PostDetailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;

public interface PostControllerDocs {

	@Operation(summary = "자료 상세 조회", description = """
		자료의 상세 정보를 조회합니다. userId를 전달하면 해당 사용자의 장바구니 항목 ID를
		cartItemId로 반환합니다. 담기지 않은 자료이거나 userId를 생략하면 null입니다.
		이미 담긴 자료를 다른 폴더에 추가할 때 cartItemId를 폴더 추가 요청의 cartItemIds에 사용합니다.
		존재하지 않는 자료 또는 사용자는 404, userId 형식 오류는 400을 반환합니다.
		""")
	ResponseEntity<SuccessResponse<PostDetailResponse>> getPostDetail(
		@PathVariable("postId") Long postId,
		@Parameter(description = "조회 사용자 ID (선택, 양수). 생략하면 cartItemId는 null", example = "1")
		@Positive(message = "사용자 ID는 양수여야 합니다.")
		@RequestParam(name = "userId", required = false) Long userId
	);
}
