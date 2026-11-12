package org.sopt.happycampus.folder.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AddCartItemsToFolderRequest(
	@ArraySchema(
		arraySchema = @Schema(description = "폴더에 넣을 장바구니 항목 ID 목록 (1개 이상, 중복 ID는 한 번만 처리)"),
		schema = @Schema(example = "1"))
	@NotEmpty(message = "폴더에 추가할 자료를 1개 이상 선택해주세요.")
	List<@NotNull(message = "장바구니 항목 ID는 비어 있을 수 없습니다.") Long> cartItemIds
) {
}
