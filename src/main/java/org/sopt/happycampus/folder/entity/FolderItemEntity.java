package org.sopt.happycampus.folder.entity;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.common.entity.BaseEntity;

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
	name = "folder_items",
	uniqueConstraints = @UniqueConstraint(name = "uk_folder_items_cart_item", columnNames = {"cart_item_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FolderItemEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "folder_id", nullable = false)
	private FolderEntity folder;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cart_item_id", nullable = false)
	private CartItemEntity cartItem;

	private FolderItemEntity(FolderEntity folder, CartItemEntity cartItem) {
		this.folder = folder;
		this.cartItem = cartItem;
	}

	public static FolderItemEntity create(FolderEntity folder, CartItemEntity cartItem) {
		return new FolderItemEntity(folder, cartItem);
	}

	public void moveTo(FolderEntity folder) {
		this.folder = folder;
	}
}
