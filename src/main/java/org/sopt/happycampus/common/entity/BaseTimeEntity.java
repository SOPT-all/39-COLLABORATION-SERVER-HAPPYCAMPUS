package org.sopt.happycampus.common.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseTimeEntity extends BaseEntity {
	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;
}
