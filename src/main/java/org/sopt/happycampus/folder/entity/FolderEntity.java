package org.sopt.happycampus.folder.entity;

import org.sopt.happycampus.common.entity.BaseTimeEntity;
import org.sopt.happycampus.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
	name = "folders",
	uniqueConstraints = @UniqueConstraint(name = "uk_folders_user_name", columnNames = {"user_id", "name"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FolderEntity extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(nullable = false, length = 100)
	private String name;

	private FolderEntity(UserEntity user, String name) {
		this.user = user;
		this.name = name;
	}

	public static FolderEntity create(UserEntity user, String name) {
		return new FolderEntity(user, name);
	}
}
