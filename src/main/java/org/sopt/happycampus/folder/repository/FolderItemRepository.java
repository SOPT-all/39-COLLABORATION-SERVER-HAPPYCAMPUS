package org.sopt.happycampus.folder.repository;

import org.sopt.happycampus.folder.entity.FolderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderItemRepository extends JpaRepository<FolderItemEntity, Long> {
}
