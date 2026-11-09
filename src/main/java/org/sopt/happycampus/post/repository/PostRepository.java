package org.sopt.happycampus.post.repository;

import org.sopt.happycampus.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
