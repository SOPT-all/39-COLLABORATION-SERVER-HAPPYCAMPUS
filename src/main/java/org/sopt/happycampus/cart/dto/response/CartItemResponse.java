package org.sopt.happycampus.cart.dto.response;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.post.entity.PostEntity;

public record CartItemResponse(
	Long cartItemId,
	Long postId,
	String title,
	String coverImageUrl,
	String description,
	Integer price,
	Integer savedYear,
	Integer pageCount,
	String fileType,
	String tableOfContents
) {
	public static CartItemResponse from(CartItemEntity cartItemEntity) {
		PostEntity postEntity = cartItemEntity.getPost();
		return new CartItemResponse(
			cartItemEntity.getId(),
			postEntity.getId(),
			postEntity.getTitle(),
			postEntity.getCoverImageUrl(),
			postEntity.getDescription(),
			postEntity.getPrice(),
			postEntity.getSavedYear(),
			postEntity.getPageCount(),
			postEntity.getFileType(),
			postEntity.getTableOfContents()
		);
	}
}
