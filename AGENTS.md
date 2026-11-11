# AGENTS.md — 39기 HappyCampus Server

> AI 코딩 에이전트(Claude Code, Codex, Cursor 등)용 작업 지침.
> 출처: 노션 「39기 HappyCampus Server」 협업 허브 (각 섹션 끝에 출처 페이지 표기).
> 노션에 근거가 없는 항목은 `TODO`로 표시했다. 추측으로 채우지 말 것.

---

## 0. 에이전트 필수 규칙 (요약)

- `main`에 직접 커밋/푸시 금지. 항상 `유형/이슈번호-짧은-설명` 브랜치 → PR.
- 패키지는 **도메인별**로 묶는다. Controller → Service → Repository 방향만 허용.
- 다른 도메인의 Controller·Repository 직접 호출 금지. 필요하면 그 도메인 **Service 메서드**를 호출.
- 모든 API 응답은 공통 응답(`SuccessResponse<T>` / `FailureResponse`)으로 반환.
- 예외는 `BaseException` + `ResultCode`로 던진다. 임의 응답 포맷 금지.
- 성공 코드는 **각 도메인 패키지**에서 `ResultCode` 구현 enum으로 정의. 공통 성공 코드 만들지 말 것.
- 아래 3장 목록 외 의존성 추가 금지(필요 시 PR에 근거를 적고 팀 합의).
- DB 스키마는 6장 ERD 기준. 컬럼·테이블 변경 시 PR에 명시하고 문서 갱신 확인.
- 커밋 하나 = 목적 하나. 형식 `타입: 변경 내용` (이슈 번호는 커밋에 넣지 않음).

---

## 1. 프로젝트 개요

- 해피캠퍼스 클론 서버 파트(합동 세미나 39기).
- 범위: 자료(document/post) 상세 조회, 장바구니(cart), 폴더(folder) 기능.

### 일정 (01. 과제 및 일정)

| 날짜 | 구분 | 내용 |
|---|---|---|
| 2026-11-04 | 공지 | 전체 킥오프 |
| 2026-11-06 | 서버 1차 과제 | 킥오프 · Repository 세팅 · 기능 명세 작성 |
| 2026-11-07 | 서버 2차 과제 | 설계 및 역할 분담 |
| 2026-11-07 | 합동 세미나 | 발표 · 전체 파트 논의 및 볼륨 조절 · QA 일정 취합 |
| 2026-11-13 | 서버 3차 과제 | 기능 구현 |
| 2026-11-19 | 서버 4차 과제 | QA 및 회고 |

- 3차 과제 체크 항목: 기능별 구현 대상·담당자, 관련 기능·API·ERD 링크, 예외·경계 조건, PR·테스트·시연 제출물, 리뷰어·완료 조건.
- 4차 과제 체크 항목: QA 대상 기능·테스트 시나리오, 버그 우선순위, 수정·재검증 기준, 회고.

---

## 2. 기술스택 (05. 기술스택)

| 영역 | 기술 | 상태 |
|---|---|---|
| Language | Java 21 | 초기 세팅 |
| Framework | Spring Boot 4.1.2 | 초기 세팅 |
| Build | Gradle | 초기 세팅 |
| Database | MySQL | 초기 계획 |
| ORM | Spring Data JPA | 초기 세팅 |
| API Docs | Springdoc / Swagger | 초기 세팅 |
| Validation | Jakarta Validation | 초기 세팅 |
| Container | Docker | 초기 계획 |
| Image Registry | Docker Hub | 초기 계획 |
| Cloud | AWS EC2 | 초기 계획 |
| CI/CD | GitHub Actions | 초기 계획 |
| Reverse Proxy / HTTPS | Nginx / HTTPS | 초기 계획 |
| Monitoring | Spring Boot Actuator | 초기 계획 |

"초기 계획" 항목은 아직 저장소에 없을 수 있다. 확인 없이 설정 파일을 생성하지 말 것.

---

## 3. 의존성 (03. 의존성)

| 범위 | 의존성 | 용도 |
|---|---|---|
| implementation | spring-boot-starter-data-jpa | Entity·Repository, DB 접근 |
| implementation | spring-boot-starter-validation | 요청 DTO Bean Validation |
| implementation | spring-boot-starter-webmvc | MVC Controller, HTTP 처리 |
| implementation | springdoc-openapi-starter-webmvc-ui | OpenAPI/Swagger UI (`SwaggerConfig`) |
| compileOnly / annotationProcessor | lombok | Lombok |
| runtimeOnly | mysql-connector-j | MySQL JDBC |
| testImplementation | spring-boot-starter-data-jpa-test | JPA·Repository 테스트 |
| testImplementation | spring-boot-starter-validation-test | 검증 규칙 테스트 |
| testImplementation | spring-boot-starter-webmvc-test | MVC·Controller 테스트 |
| testCompileOnly / testAnnotationProcessor | lombok | 테스트 Lombok |
| testRuntimeOnly | junit-platform-launcher | JUnit Platform 실행 |

---

## 4. 빌드·실행·테스트

```bash
./gradlew build      # 빌드
./gradlew bootRun    # 로컬 실행
./gradlew test       # 테스트
```

- 위 명령은 Gradle Wrapper 표준 기준. 프로파일·환경변수·로컬 MySQL 설정은 `TODO: 저장소 확인 후 기록`.
- 더미데이터: 노션 「더미데이터」 페이지의 Google Sheets 양식 사용. 초기화 방법(SQL/`data.sql`/스크립트)은 `TODO`.

---

## 5. 아키텍처 (03. 아키텍처 구조)

### 패키지 구조

```
<base-package>/            # TODO: 실제 base package 확인 (예시는 com.example)
├── post/                  # 도메인 예시
│   ├── controller/        # HTTP 진입점
│   ├── dto/               # 요청·응답 계약
│   ├── service/           # 유스케이스
│   ├── entity/            # 영속 상태·불변식
│   └── repository/        # 조회·저장
├── cart/
├── folder/
├── member/ (user)
└── common/
    ├── exception/         # 공통 오류 변환
    └── config/            # 여러 도메인 공통 설정 (예: SwaggerConfig)
```

- 한 기능의 Controller·DTO·Service·Entity·Repository는 **소유 도메인 안에 함께** 둔다.
- `common`에는 실제로 여러 도메인에 적용되는 것만. 특정 도메인 정책·편의 메서드를 옮기지 말 것.
- Service 간 순환 의존이 생기면 조정용 유스케이스 Service를 만들고 소유 도메인을 정한다.

### 레이어 책임

| 레이어 | 할 일 |
|---|---|
| Controller | 경로·HTTP 메서드 정의, 요청 DTO 수신, 형식 검증(`@Valid`) 실행, 인증 주체 전달, Service 호출, 상태 코드·응답 DTO 반환 |
| Service | 유스케이스 흐름, 권한·상태·중복 등 비즈니스 규칙, Repository·타 도메인 Service 조합, **트랜잭션 경계**, 결과 반환 |
| Entity | 저장 상태, 불변식, 연관관계, 의미 있는 상태 변경 메서드 |
| Repository | 조회·저장, 조건 검색·페이징 쿼리 |
| DTO | API 입출력 필드, 입력 형식 제약, 공개 필드 제한 |

- 판단 예: "제목은 비어 있으면 안 된다" → 요청 DTO 검증. "마감된 게시글은 수정 불가" → Service 규칙(Entity 상태 변경 메서드에서 불변식 보장 가능).
- Entity를 API 응답으로 직접 반환하지 말 것. Controller에서 응답 DTO로 변환.

---

## 6. 네이밍 컨벤션 (03. 네이밍 컨벤션)

| 대상 | 표기 | 예시 |
|---|---|---|
| 패키지 | 소문자 | `com.example.post.service` |
| 클래스·인터페이스 | PascalCase | `PostService`, `PostRepository` |
| 메서드·변수 | camelCase | `findPostById()`, `postCount` |
| 상수 | UPPER_SNAKE_CASE | `MAX_PAGE_SIZE` |
| 요청·응답 DTO | 역할 + Request/Response | `CreatePostRequest`, `PostResponse` |
| Entity·예외·테스트 | 역할 + 접미사 | `PostEntity`, `PostNotFoundException`, `PostServiceTest` |
| DB 테이블·컬럼 | snake_case | `posts`, `created_at` |
| API 경로 | `/api/**` 아래 명사형 리소스 | `GET /api/posts/{postId}` |
| JSON 필드 | camelCase | `postId`, `createdAt` |

---

## 7. 공통 응답 & 예외 처리 (03. 공통 응답 및 예외 처리)

### 구성 요소

| 타입 | 설명 |
|---|---|
| `ResultCode` | `HttpStatus`와 메시지를 제공하는 인터페이스 |
| `ErrorCode` | `ResultCode`를 구현한 공통 오류 enum |
| `SuccessCode` | 각 도메인 패키지에서 `ResultCode` 구현으로 정의 (공통 성공 코드 없음) |
| `ApiResponse` | `status`, `code`, `message` 인터페이스 + 성공/실패 정적 팩토리 |
| `SuccessResponse<T>` | record(`status`, `code`, `message`, `data`). data 없으면 null |
| `FailureResponse` | record(`status`, `code`, `message`) |

### 응답 예시

```json
{ "status": 201, "code": "POST_CREATED", "message": "게시글이 생성되었습니다.", "data": { "postId": 1 } }
```
```json
{ "status": 400, "code": "INVALID_INPUT_VALUE", "message": "입력값이 올바르지 않습니다." }
```

### 공통 ErrorCode

| HTTP | ErrorCode |
|---|---|
| 400 | `MISSING_REQUEST_PARAMETER`, `MISSING_REQUEST_HEADER`, `INVALID_HEADER`, `INVALID_REQUEST_BODY`, `INVALID_PARAMETER_TYPE`, `INVALID_INPUT_VALUE` |
| 404 | `NOT_FOUND` |
| 405 | `METHOD_NOT_ALLOWED` |
| 415 | `UNSUPPORTED_MEDIA_TYPE` |
| 500 | `INTERNAL_SERVER_ERROR` |

### 예외 매핑 (GlobalExceptionHandler)

| 예외 | 결과 |
|---|---|
| `BaseException` | 담긴 `ResultCode`의 상태·코드·메시지 |
| `IllegalArgumentException` | `INVALID_INPUT_VALUE` 기본 메시지 |
| 필수 파라미터/헤더 누락 | `MISSING_REQUEST_PARAMETER` / `MISSING_REQUEST_HEADER` |
| 읽을 수 없는 요청 본문 | `INVALID_REQUEST_BODY` |
| 파라미터 타입 불일치 | `INVALID_PARAMETER_TYPE` |
| DTO·메서드·제약조건 검증 실패 | `INVALID_INPUT_VALUE` (첫 번째 검증 메시지 사용, 없으면 기본 메시지) |
| 리소스 없음 / 메서드 불일치 / 미지원 미디어 타입 | 404 / 405 / 415 ErrorCode |
| 그 밖의 `Exception` | 예외 메시지·스택 로그 후 `INTERNAL_SERVER_ERROR` |

- 도메인 오류(예: 장바구니 중복 담기, 폴더 없음)는 도메인별 `ResultCode` enum을 만들어 `BaseException`으로 던진다.

---

## 8. 데이터 모델 (06. ERD)

| 테이블 | 컬럼 |
|---|---|
| `users` | `id` bigint PK, `created_at` datetime, `updated_at` datetime |
| `posts` | `id` bigint PK, `category` varchar(30) NN, `title` varchar(255) NN, `description` varchar, `price` int, `cover_image_url` varchar(255), `saved_year` int, `file_type` varchar(30), `page_count` int, `table_of_contents` varchar, `content` varchar, `created_at`, `updated_at` |
| `image_url` | `id` bigint PK, `post_id` bigint NN FK→posts, `image_url` varchar(255) NN |
| `cart_items` | `id` bigint PK, `user_id` bigint NN FK→users, `post_id` bigint NN FK→posts, `created_at` |
| `folders` | `id` bigint PK, `user_id` bigint NN FK→users, `name` varchar(100) NN, `created_at`, `updated_at` |
| `folder_items` | `id` bigint PK, `folder_id` bigint NN FK→folders, `cart_item_id` bigint NN FK→cart_items, `created_at` |

관계: users 1:N folders, users 1:N cart_items, posts 1:N cart_items, posts 1:N image_url, folders 1:N folder_items, cart_items 1:N folder_items.

---

## 9. 기능·API 명세 & 역할 분담 (06. 기능·API 명세서 / 역할 분담)

| ID | 도메인 | 기능 | 메서드 | 연관 화면 | 담당 |
|---|---|---|---|---|---|
| 2 | document | 자료 상세 조회 | GET | 자료 상세 조회 화면 | 김진웅 |
| 3 | cart | 장바구니 담기 | POST | 메인, 자료 상세 조회 화면 | 김진웅 |
| 4 | cart | 장바구니 자료 목록 조회 | GET | 장바구니 화면 | 김현준 |
| 5 | cart | 장바구니 자료 삭제 | DELETE | 장바구니 화면 | 김현준 |
| 7 | folder | 폴더 목록 조회 | GET | 폴더 선택 화면 | 김강민 |
| 8 | folder | 폴더 생성 | POST | 폴더 선택 화면 | 김강민 |
| 10 | folder | 폴더에 자료 추가 (장바구니 자료 → 폴더) | POST | 폴더 선택 화면 | 김강민 |

- 우선순위: 전부 **상**.
- API 경로: 노션 API 탭 미작성(placeholder `POST /auth/register`만 존재). 네이밍 규칙(`/api/**` 명사형)에 맞춰 정하고 PR에 명시. `TODO`.
- 리뷰어: 작성자 제외 서버 팀원 전원(10장 참고).

---

## 10. Git 워크플로 (04. 협업 컨벤션)

### 브랜치

- `main`: 통합 기준. 직접 푸시 금지, PR로만 변경. 항상 배포 가능한 상태 유지.
- 작업 브랜치: 최신 `main`에서 생성, PR 종료 후 삭제.
- 형식: `유형/이슈번호-짧은-설명`

| 유형 | 용도 | 예시 |
|---|---|---|
| feat | 기능 추가 | `feat/123-post-create` |
| fix | 일반 버그 수정 | `fix/124-post-list-error` |
| chore | 설정·빌드·의존성 | `chore/125-gradle-config` |
| hotfix | 배포 서비스 긴급 장애 | `hotfix/126-login-failure` |

### 커밋

- 형식: `타입: 변경 내용` (예: `feat: 게시글 생성 API 구현`)
- 이슈 번호는 커밋에 넣지 않고 브랜치·PR에서 연결.
- 타입: `feat`, `fix`, `refactor`(동작 변화 없음), `docs`, `test`, `chore`

### Issue 템플릿 (`.github/ISSUE_TEMPLATE/`)

- ✨ Feat — title `[FEAT] `, label `feature`, 필드: Describe(필수), Tasks 체크리스트(필수), Appendix
- 🚨 Fix — title `[FIX] `, label `bug`, 필드: Describe(필수), Fix Plan(필수), Appendix
- 🔨 Refactor — title `[REFACTOR] `, label `refactor`, 필드: Describe(필수), Refactor Plan(필수), Appendix

### PR 템플릿

PR 제목: `[TYPE/#issue_num] subject`

```markdown
## 📌 Related Issue
- Closes #

## 📤 Tasks

## 📸 Screenshot

## 💌 To Reviewer
```

### 리뷰 & 머지

- 리뷰어: 작성자 제외 **서버 팀원 전원** 지정. 변경 목적·테스트 결과·확인 필요 부분 기재.
- 리뷰 기한: 요청 후 1일 이내 (어려우면 예상 시점 코멘트).
- 머지 조건: 전원 Approve + 미해결 댓글 없음 + 충돌 해결 + API·DB·설정 변경 시 문서 갱신 확인.
- 충돌은 작성자가 작업 브랜치에서 해결 후 테스트 재확인. 필요 시 재리뷰.
- 머지 담당: **PR 작성자 본인**.

---

## 11. 팀 그라운드룰 (02. 그라운드룰)

- 연락: 합세서버 단톡방. 1시간 이내 답장(바쁘면 확인 여부·답장 가능 시간 공유).
- 회의: 과제 제출 전날 전원 참여 — 진행 상황·일정 변동·결정 안건 확인.
- 블로커: 자력 해결 불가하거나 타인 작업·마감에 영향이 예상되면 기다리지 말고 즉시 공유.
- 에이전트는 블로커·스펙 불명확 사항을 발견하면 임의로 진행하지 말고 PR/이슈에 질문으로 남긴다.

---

## 민감 설정 파일 접근 금지

- `application-prod.yml`은 경로와 관계없이 Git 추적·스테이징·커밋·푸시 대상에 포함하지 않는다. `.gitignore`의 제외 규칙을 유지하며, 강제 추가(`git add -f`)하지 않는다.
- 에이전트는 경로와 관계없이 `application-prod.yml`의 내용을 읽거나 검색·출력·복사하지 않는다.
- 전체 저장소 검색, diff 확인, 로그 수집 등에서도 해당 파일의 내용이 노출되지 않도록 제외한다.
- 이 규칙은 로컬·EC2·컨테이너 및 Git 이력에 있는 동일 이름 파일에도 적용한다.
- 설정 확인이 필요한 경우 사용자에게 필요한 항목만 직접 확인하도록 안내하며, 파일 내용이나 비밀번호를 공유하도록 요청하지 않는다.

---

## 12. ⚠️ 확인 필요 / TODO

- [ ] 실제 base package 이름 (노션 예시는 `com.example`)
- [ ] 각 기능의 API 경로·요청/응답 스펙 (API 탭 미작성)
- [ ] 기능 ID 1, 6, 9 누락 여부 확인
- [ ] 도메인 명칭 불일치: 명세서는 `document`, ERD·컨벤션 예시는 `post/posts` → 패키지명 통일 필요
- [ ] `folder_items`가 `cart_item_id`를 참조 → 장바구니 자료 삭제(ID 5) 시 폴더 항목 처리 정책(FK 제약/cascade) 결정 필요
- [ ] 인증 방식(사용자 식별: 헤더? 토큰?) 미정 — Controller의 "인증 주체 전달" 방식
- [ ] 로컬 실행 프로파일·환경변수·DB 설정
- [ ] 더미데이터 적재/초기화 방법
- [ ] 테스트 작성 범위·기준 (PR에 테스트 결과를 적도록 되어 있으나 기준 없음)
- [ ] 노션 AGENTS.md 페이지 "초안 작성 및 팀 합의 필요" → 본 문서 팀 합의 후 상태 갱신
