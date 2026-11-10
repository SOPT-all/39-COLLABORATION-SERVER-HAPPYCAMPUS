package org.sopt.happycampus.post.service;

import org.sopt.happycampus.post.entity.PostEntity;
import org.sopt.happycampus.post.exception.PostNotFoundException;
import org.sopt.happycampus.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * 자료 조회 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class PostService {
    // DB에서 자료를 조회할 때 사용할 Repo
    private final PostRepository postRepository;

    /**
     * ID에 해당하는 자료를 조회
     *
     * @param postId 조회할 자료 ID
     * @return 조회한 자료
     * @throws PostNotFoundException 자료가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public PostEntity findPostById(Long postId) {
        return postRepository.findWithImagesById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

}
