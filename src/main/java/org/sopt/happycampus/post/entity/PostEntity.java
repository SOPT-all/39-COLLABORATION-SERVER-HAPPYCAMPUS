package org.sopt.happycampus.post.entity;

import java.util.ArrayList;
import java.util.List;

import org.sopt.happycampus.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostEntity extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 30)
	private String category;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	private Integer price;

	private String coverImageUrl;

	// 저작 연월. 예: 202602
	private Integer savedYear;

	@Column(length = 30)
	private String fileType;

	private Integer pageCount;

	@Column(columnDefinition = "TEXT")
	private String tableOfContents;

	@Column(columnDefinition = "TEXT")
	private String content;

	@OneToMany(mappedBy = "post")
	private List<PostImageEntity> images = new ArrayList<>();
}
