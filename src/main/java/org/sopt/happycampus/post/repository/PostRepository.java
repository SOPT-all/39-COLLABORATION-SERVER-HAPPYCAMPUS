package org.sopt.happycampus.post.repository;

import org.sopt.happycampus.post.entity.PostEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 자료 조회 및 저장을 위한 Repository
 */
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    /**
     * 자료와 연결된 이미지를 함께 조회
     * @param id 조회할 자료 ID
     * @return 이미지가 포함된 자료. 자료가 없으면 빈 Optional
     */
    @EntityGraph(attributePaths = "images")
    Optional<PostEntity> findWithImagesById(Long id);
}
