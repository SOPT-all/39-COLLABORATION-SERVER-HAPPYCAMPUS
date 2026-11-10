package org.sopt.happycampus.folder.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CreateFolderRequestTest {
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

	@ParameterizedTest
	@ValueSource(strings = {"a", "자기소개서", "자소서 2026", "12345678"})
	@DisplayName("공백 포함 8자 이하의 이름은 통과한다")
	void validName(String name) {
		assertThat(validator.validate(new CreateFolderRequest(name))).isEmpty();
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"   ", "123456789", "자기소개서 모음집"})
	@DisplayName("비어 있거나, 공백만 있거나, 8자를 넘는 이름은 거부한다")
	void invalidName(String name) {
		assertThat(validator.validate(new CreateFolderRequest(name))).isNotEmpty();
	}
}
