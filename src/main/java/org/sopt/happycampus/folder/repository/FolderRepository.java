package org.sopt.happycampus.folder.repository;

import java.util.List;

import org.sopt.happycampus.folder.dto.response.FolderResponse;
import org.sopt.happycampus.folder.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FolderRepository extends JpaRepository<FolderEntity, Long> {

	@Query("""
		select new org.sopt.happycampus.folder.dto.response.FolderResponse(f.id, f.name, count(fi.id))
		from FolderEntity f
		left join FolderItemEntity fi on fi.folder = f
		where f.user.id = :userId
		group by f.id, f.name, f.createdAt
		order by f.createdAt asc, f.id asc
		""")
	List<FolderResponse> findAllWithItemCountByUserId(@Param("userId") Long userId);
}
