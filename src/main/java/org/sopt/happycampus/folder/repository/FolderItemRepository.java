package org.sopt.happycampus.folder.repository;

import java.util.Collection;
import java.util.List;

import org.sopt.happycampus.folder.entity.FolderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderItemRepository extends JpaRepository<FolderItemEntity, Long> {

	void deleteByCartItemId(Long cartItemId);

	List<FolderItemEntity> findAllByFolderIdAndCartItemIdIn(Long folderId, Collection<Long> cartItemIds);
}
