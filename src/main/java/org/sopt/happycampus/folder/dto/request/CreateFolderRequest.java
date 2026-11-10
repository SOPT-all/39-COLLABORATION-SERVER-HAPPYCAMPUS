package org.sopt.happycampus.folder.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFolderRequest(
	@Schema(description = "폴더 이름 (앞뒤 공백 제거 후 공백 포함 8자 이하)", example = "자기소개서")
	@NotBlank(message = "폴더 이름을 입력해주세요.")
	@Size(max = 8, message = "폴더 이름은 공백 포함 8자 이하로 입력해주세요.")
	String name
) {
	// 앞뒤 공백을 제거한 이름으로 검증·중복 확인·저장한다. 가운데 공백은 유지한다.
	public CreateFolderRequest {
		if (name != null) {
			name = name.strip();
		}
	}
}
