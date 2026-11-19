# 자료 상세 조회

`GET /api/v1/posts/{postId}`

자료 상세 정보와 조회 사용자의 장바구니 항목 ID를 공통 성공 응답으로 반환합니다.

| 입력 | 위치 | 필수 | 설명 |
| --- | --- | --- | --- |
| `postId` | path | 예 | 조회할 자료 ID |
| `userId` | query | 아니오 | 조회 사용자 ID. 양수이며 존재하는 사용자여야 합니다. |

예: `GET /api/v1/posts/1?userId=1`

기존 응답의 `data`에 `cartItemId` 필드가 추가됩니다. 나머지 상세 필드는 유지됩니다.

| 조건 | `data.cartItemId` |
| --- | --- |
| 조회 사용자가 해당 자료를 장바구니에 담음 | 해당 사용자의 장바구니 항목 ID |
| 조회 사용자가 해당 자료를 담지 않음 | `null` |
| `userId` 생략 | `null` |
| 다른 사용자만 해당 자료를 담음 | `null` |

`cartItemId`는 `null`일 때도 JSON 응답에 포함됩니다. `userId`가 없는 기존 요청도 계속 사용할 수 있습니다.

| 상황 | HTTP | 응답 코드 |
| --- | --- | --- |
| 정상 조회 | 200 | `POST_DETAIL_FOUND` |
| 자료 없음 | 404 | `POST_NOT_FOUND` |
| 지정한 사용자 없음 | 404 | `USER_NOT_FOUND` |
| `userId`가 0 또는 음수 | 400 | `INVALID_INPUT_VALUE` |
| `userId`를 정수로 변환할 수 없음 | 400 | `INVALID_PARAMETER_TYPE` |

자료 존재 여부를 먼저 확인하고, 사용자가 지정된 경우 사용자와 장바구니 상태를 조회합니다.

## 이미 담긴 자료를 다른 폴더에 추가

1. 상세 조회 시 현재 사용자의 `userId`를 쿼리로 전달하고 `data.cartItemId`를 보관합니다.
2. 장바구니 중복 담기는 기존과 같이 `409 CART_ITEM_DUPLICATED`를 반환합니다.
3. 다른 폴더에 추가할 때 상세 응답의 ID를 사용해 아래 API를 호출합니다.

`POST /api/v1/users/{userId}/folders/{folderId}/cart-items`

```json
{ "cartItemIds": [42] }
```

`cartItemId`가 `null`이면 먼저 장바구니에 담고, 담기 성공 응답의 ID를 사용합니다.
상세 조회 후 장바구니 항목이 삭제되어 폴더 추가가 실패하면 상세 정보를 다시 조회해야 합니다.

## 리뷰 확인 사항

현재 장바구니·폴더 API는 호출자가 전달한 `userId`로 사용자를 구분합니다. 이번 변경도 이 방식을 따르며,
인증을 새로 도입하지 않습니다. 상세 조회에서 `userId`를 선택 쿼리로 받는 계약을 프론트엔드와 확인해야 합니다.
