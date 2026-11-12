package org.sopt.happycampus.folder.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class AddCartItemsToFolderRequestTest {
	private static ValidatorFactory factory;
	private static Validator validator;

	@BeforeAll
	static void setUp() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@AfterAll
	static void tearDown() {
		factory.close();
	}

	@Test
	@DisplayName("장바구니 항목 ID가 1개 이상이면 통과한다")
	void valid() {
		assertThat(validator.validate(new AddCartItemsToFolderRequest(List.of(1L)))).isEmpty();
		assertThat(validator.validate(new AddCartItemsToFolderRequest(List.of(1L, 2L, 2L)))).isEmpty();
	}

	@Test
	@DisplayName("목록이 없거나 비어 있으면 거부한다")
	void emptyOrNull() {
		assertThat(validator.validate(new AddCartItemsToFolderRequest(List.of()))).isNotEmpty();
		assertThat(validator.validate(new AddCartItemsToFolderRequest(null))).isNotEmpty();
	}

	@Test
	@DisplayName("목록 안에 null이 있으면 거부한다")
	void nullElement() {
		assertThat(validator.validate(new AddCartItemsToFolderRequest(Arrays.asList(1L, null)))).isNotEmpty();
	}
}
