package org.sopt.happycampus.folder.repository;

import org.sopt.happycampus.folder.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<FolderEntity, Long> {
}
