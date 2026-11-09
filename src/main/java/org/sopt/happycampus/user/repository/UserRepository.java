package org.sopt.happycampus.user.repository;

import org.sopt.happycampus.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
