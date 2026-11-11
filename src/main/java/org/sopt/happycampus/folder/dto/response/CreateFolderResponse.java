package org.sopt.happycampus.folder.dto.response;

import org.sopt.happycampus.folder.entity.FolderEntity;

public record CreateFolderResponse(
	Long folderId,
	String name
) {
	public static CreateFolderResponse from(FolderEntity folder) {
		return new CreateFolderResponse(folder.getId(), folder.getName());
	}
}
