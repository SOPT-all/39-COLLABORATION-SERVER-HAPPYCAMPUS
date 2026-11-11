package org.sopt.happycampus.cart.repository;

import static org.sopt.happycampus.cart.entity.QCartItemEntity.*;
import static org.sopt.happycampus.folder.entity.QFolderItemEntity.*;
import static org.sopt.happycampus.post.entity.QPostEntity.*;

import java.util.List;

import org.sopt.happycampus.cart.entity.CartItemEntity;
import org.sopt.happycampus.post.entity.PostCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CartItemRepositoryImpl extends QuerydslRepositorySupport implements CartItemRepositoryCustom {
	@Autowired
	private JPAQueryFactory queryFactory;

	public CartItemRepositoryImpl() {
		super(CartItemEntity.class);
	}

	@Override
	public Page<CartItemEntity> getCartItemsBySearchOption(Pageable pageable, Long userId, PostCategory category, Long folderId) {

		List<CartItemEntity> cartItems = queryFactory
			.selectFrom(cartItemEntity)
			.join(cartItemEntity.post, postEntity).fetchJoin()
			.where(cartItemEntity.user.id.eq(userId), eqCategory(category), eqFolderId(folderId))
			.orderBy(cartItemEntity.createdAt.desc(), cartItemEntity.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount = queryFactory
			.select(cartItemEntity.count())
			.from(cartItemEntity)
			.where(cartItemEntity.user.id.eq(userId), eqCategory(category), eqFolderId(folderId))
			.fetchOne();

		return new PageImpl<>(
			cartItems,
			pageable,
			totalCount != null ? totalCount : 0L);
	}

	private BooleanExpression eqFolderId(Long folderId) {
		if (folderId == null) return null;

		return JPAExpressions
			.selectOne()
			.from(folderItemEntity)
			.where(
				folderItemEntity.cartItem.eq(cartItemEntity),
				folderItemEntity.folder.id.eq(folderId)
			)
			.exists();
	}

	private BooleanExpression eqCategory(PostCategory category) {
		return category == null ? null : cartItemEntity.post.category.eq(category);
	}
}
