package org.sopt.happycampus.cart.entity;

import org.sopt.happycampus.common.entity.BaseEntity;
import org.sopt.happycampus.post.entity.PostEntity;
import org.sopt.happycampus.user.entity.UserEntity;

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
	name = "cart_items",
	uniqueConstraints = @UniqueConstraint(name = "uk_cart_items_user_post", columnNames = {"user_id", "post_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItemEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;

	private CartItemEntity(UserEntity user, PostEntity post) {
		this.user = user;
		this.post = post;
	}

	public static CartItemEntity create(UserEntity user, PostEntity post) {
		return new CartItemEntity(user, post);
	}
}
