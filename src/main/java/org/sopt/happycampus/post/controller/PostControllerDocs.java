package org.sopt.happycampus.post.controller;

import org.sopt.happycampus.common.response.SuccessResponse;
import org.sopt.happycampus.post.dto.PostDetailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;

public interface PostControllerDocs {

	@Operation(summary = "자료 상세 조회", description = "자료의 상세 정보를 조회합니다.")
	ResponseEntity<SuccessResponse<PostDetailResponse>> getPostDetail(
		@PathVariable("postId") Long postId
	);
}
