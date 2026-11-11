package org.sopt.happycampus.post.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.happycampus.common.response.ApiResponse;
import org.sopt.happycampus.common.response.SuccessResponse;
import org.sopt.happycampus.post.code.PostSuccessCode;
import org.sopt.happycampus.post.dto.PostDetailResponse;
import org.sopt.happycampus.post.entity.PostEntity;
import org.sopt.happycampus.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 자료 관련 HTTP 요청을 처리하는 Controller
 */
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    /**
     * 자료 조회 비즈니스 로직을 처리하는 Service.
     */
    private final PostService postService;

    /**
     * 자료 ID로 상세 정보를 조회
     * @param postId 조회할 자료 ID
     * @return 자료 상세 정보가 포함된 공통 성공 응답
     */
    @GetMapping("/{postId}")
    public ResponseEntity<SuccessResponse<PostDetailResponse>> getPostDetail(
            @PathVariable("postId") Long postId
    ) {
        PostEntity post = postService.findPostById(postId);
        PostDetailResponse response = PostDetailResponse.from(post);

        return ResponseEntity.status(PostSuccessCode.POST_DETAIL_FOUND.getStatus())
                .body(ApiResponse.success(PostSuccessCode.POST_DETAIL_FOUND, response));
    }
}

