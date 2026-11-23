package org.sopt.happycampus.post.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.sopt.happycampus.cart.service.CartItemService;
import org.sopt.happycampus.folder.dto.response.FolderSummaryResponse;
import org.sopt.happycampus.folder.service.FolderItemService;
import org.sopt.happycampus.post.dto.PostDetailResponse;
import org.sopt.happycampus.post.entity.PostEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 자료 상세 정보와 사용자의 장바구니 상태를 조합하는 유스케이스 서비스.
 * CartItemService가 PostService를 사용하므로 상세 조회 조합을 분리해 순환 의존을 방지한다.
 */
@Service
@RequiredArgsConstructor
public class PostDetailService {
    private final PostService postService;
    private final CartItemService cartItemService;
    private final FolderItemService folderItemService;

    /**
     * 자료와 이미지, 조회 사용자의 장바구니 항목 ID를 하나의 읽기 트랜잭션에서 조회한다.
     *
     * @param postId 조회할 자료 ID
     * @param userId 조회 사용자 ID, 생략하면 장바구니 상태를 조회하지 않는다
     * @return 상세 응답 (사용자 미지정 또는 미담기 상태이면 cartItemId는 null)
     */
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId, Long userId) {
        PostEntity post = postService.findPostById(postId);
        Long cartItemId = userId == null ? null : cartItemService.findCartItemId(userId, postId);
		List<FolderSummaryResponse> folders = cartItemId == null
            ? List.of()
            : folderItemService.findFoldersByCartItemId(cartItemId);

        return PostDetailResponse.from(post, cartItemId, folders);
    }
}
