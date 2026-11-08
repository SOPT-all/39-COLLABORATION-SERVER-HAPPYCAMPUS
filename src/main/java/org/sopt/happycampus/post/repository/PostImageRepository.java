package org.sopt.happycampus.post.repository;

import org.sopt.happycampus.post.entity.PostImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImageEntity, Long> {
}
