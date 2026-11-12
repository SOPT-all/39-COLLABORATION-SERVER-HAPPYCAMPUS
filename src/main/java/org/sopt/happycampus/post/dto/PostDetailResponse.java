package org.sopt.happycampus.post.dto;

import java.util.List;

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
        List<String> imageUrls
) {
    /**
     * 자료 Entity를 상세 조회 응답으로 변환한다
     *
     * @param post 조회한 자료
     * @return 자료 상세 응답
     */
    public static PostDetailResponse from(PostEntity post) {
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
                post.getImages().stream().map(PostImageEntity::getImageUrl).toList()
        );
    }
}
