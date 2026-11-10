package org.sopt.happycampus.folder.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFolderRequest(
	@Schema(description = "폴더 이름 (공백 포함 8자 이하)", example = "자기소개서")
	@NotBlank(message = "폴더 이름을 입력해주세요.")
	@Size(max = 8, message = "폴더 이름은 공백 포함 8자 이하로 입력해주세요.")
	String name
) {
}
