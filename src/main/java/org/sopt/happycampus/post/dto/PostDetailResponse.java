package org.sopt.happycampus.post.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import org.sopt.happycampus.post.entity.PostCategory;
import org.sopt.happycampus.post.entity.PostEntity;
import org.sopt.happycampus.post.entity.PostImageEntity;

/**
 * 자료 상세 조회 응답
 *
 * @param postId 자료 ID
 * @param category 자료 카테고리
 * @param title 자료 제목
 * @param description 자료 설명
 * @param price 자료 가격
 * @param coverImageUrl 표지 이미지 URL
 * @param savedYear 저작 연월
 * @param fileType 파일 형식
 * @param pageCount 페이지 수
 * @param tableOfContents 목차
 * @param content 자료 본문
 * @param imageUrls 자료 이미지 URL 목록
 * @param cartItemId 조회 사용자의 장바구니 항목 ID (사용자 미지정 또는 미담기 시 null)
 */
public record PostDetailResponse (
        Long postId,
        PostCategory category,
        String title,
        String description,
        Integer price,
        String coverImageUrl,
        Integer savedYear,
        String fileType,
        Integer pageCount,
        String tableOfContents,
        String content,
        List<String> imageUrls,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(description = "조회 사용자의 장바구니 항목 ID. userId 생략 또는 미담기 시 null",
                example = "42", types = {"integer", "null"}, format = "int64")
        Long cartItemId
) {
    /**
     * 자료 Entity를 상세 조회 응답으로 변환한다
     *
     * @param post 조회한 자료
     * @param cartItemId 사용자의 장바구니 항목 ID, 없으면 null
     * @return 자료 상세 응답
     */
    public static PostDetailResponse from(PostEntity post, Long cartItemId) {
        return new PostDetailResponse(
                post.getId(),
                post.getCategory(),
                post.getTitle(),
                post.getDescription(),
                post.getPrice(),
                post.getCoverImageUrl(),
                post.getSavedYear(),
                post.getFileType(),
                post.getPageCount(),
                post.getTableOfContents(),
                post.getContent(),
                post.getImages().stream().map(PostImageEntity::getImageUrl).toList(),
                cartItemId
        );
    }
}
