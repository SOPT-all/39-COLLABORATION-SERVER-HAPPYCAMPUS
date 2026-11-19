package org.sopt.happycampus.post.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.happycampus.common.response.ApiResponse;
import org.sopt.happycampus.common.response.SuccessResponse;
import org.sopt.happycampus.post.code.PostSuccessCode;
import org.sopt.happycampus.post.dto.PostDetailResponse;
import org.sopt.happycampus.post.service.PostDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 자료 관련 HTTP 요청을 처리하는 Controller
 */
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController implements PostControllerDocs{

    /**
     * 자료와 사용자별 장바구니 상태를 조합하는 Service.
     */
    private final PostDetailService postDetailService;

    /**
     * 자료 ID로 상세 정보를 조회
     * @param postId 조회할 자료 ID
     * @param userId 조회 사용자 ID, 생략 가능
     * @return 자료 상세 정보가 포함된 공통 성공 응답
     */
    @GetMapping("/{postId}")
    public ResponseEntity<SuccessResponse<PostDetailResponse>> getPostDetail(
            @PathVariable("postId") Long postId,
            @RequestParam(name = "userId", required = false) Long userId
    ) {
        PostDetailResponse response = postDetailService.getPostDetail(postId, userId);

        return ResponseEntity.status(PostSuccessCode.POST_DETAIL_FOUND.getStatus())
                .body(ApiResponse.success(PostSuccessCode.POST_DETAIL_FOUND, response));
    }
}

