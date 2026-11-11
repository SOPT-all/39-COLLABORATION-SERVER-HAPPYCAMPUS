package org.sopt.happycampus.folder.dto.response;

public record FolderResponse(
	Long folderId,
	String name,
	Long itemCount
) {
}
