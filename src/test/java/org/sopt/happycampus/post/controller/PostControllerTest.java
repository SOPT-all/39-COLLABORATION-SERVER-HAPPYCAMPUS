package org.sopt.happycampus.post.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import tools.jackson.databind.ObjectMapper;

/**
 * 실제 HTTP 요청으로 자료 조회 상태 코드와 공통 응답을 검증한다.
 * 테스트 프로파일의 메모리 DB만 사용한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostControllerTest {

    private static final long POST_ID = 900001L;
    private static final long USER_ID = 900001L;
    private static final long OTHER_USER_ID = 900002L;
    private static final long CART_ITEM_ID = 900003L;
    private static final long FOLDER_ID = 900004L;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    @AfterEach
    void clearTestPost() {
        jdbcTemplate.update("delete from folder_items where folder_id = ?", FOLDER_ID);
        jdbcTemplate.update("delete from folders where id = ?", FOLDER_ID);
        jdbcTemplate.update("delete from cart_items where post_id = ?", POST_ID);
        jdbcTemplate.update("delete from image_url where post_id = ?", POST_ID);
        jdbcTemplate.update("delete from posts where id = ?", POST_ID);
        jdbcTemplate.update("delete from users where id in (?, ?)", USER_ID, OTHER_USER_ID);
    }

    /**
     * 자료와 여러 이미지가 HTTP 200 공통 응답에 포함된다.
     */
    @Test
    void returnsPostWithImages() throws Exception {
        insertPost();
        jdbcTemplate.update("insert into image_url (post_id, image_url) values (?, ?)",
                POST_ID, "https://example.com/preview-1.jpg");
        jdbcTemplate.update("insert into image_url (post_id, image_url) values (?, ?)",
                POST_ID, "https://example.com/preview-2.jpg");

        var response = getPost();
        var body = objectMapper.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(body.path("status").asInt()).isEqualTo(200);
        assertThat(body.path("code").asText()).isEqualTo("POST_DETAIL_FOUND");
        assertThat(body.path("data").path("postId").asLong()).isEqualTo(POST_ID);
        assertThat(body.path("data").path("title").asText()).isEqualTo("자료 조회 테스트");
        assertThat(body.path("data").has("cartItemId")).isTrue();
        assertThat(body.path("data").path("cartItemId").isNull()).isTrue();
        assertThat(body.path("data").path("imageUrls").size()).isEqualTo(2);
        assertThat(body.path("data").path("imageUrls").toString())
                .contains("https://example.com/preview-1.jpg", "https://example.com/preview-2.jpg");
    }

    /**
     * 이미지가 없는 자료도 정상 조회되며 이미지 목록은 빈 배열이다.
     */
    @Test
    void returnsPostWithoutImages() throws Exception {
        insertPost();

        var response = getPost();
        var body = objectMapper.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(body.path("data").path("imageUrls").isArray()).isTrue();
        assertThat(body.path("data").path("imageUrls").size()).isZero();
    }

    /**
     * 존재하지 않는 자료에는 HTTP 404와 도메인 오류 코드를 반환한다.
     */
    @Test
    void returnsNotFoundForMissingPost() throws Exception {
        var response = getPost();
        var body = objectMapper.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(body.path("status").asInt()).isEqualTo(404);
        assertThat(body.path("code").asText()).isEqualTo("POST_NOT_FOUND");
        assertThat(body.path("message").asText()).isEqualTo("자료를 찾을 수 없습니다.");
    }

    /** 기존 장바구니 항목 ID를 상세 응답으로 받아 중복 담기 이후에도 폴더에 추가한다. */
    @Test
    void addsPreviouslySavedPostToFolderUsingDetailResponse() throws Exception {
        insertPost();
        insertUser(USER_ID);
        insertCartItem(CART_ITEM_ID, USER_ID);
        jdbcTemplate.update("""
                insert into folders (id, user_id, name, created_at, updated_at)
                values (?, ?, '다른 폴더', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """, FOLDER_ID, USER_ID);

        var detail = getPost("?userId=" + USER_ID);
        var detailBody = objectMapper.readTree(detail.body());
        assertThat(detail.statusCode()).isEqualTo(200);
        long cartItemId = detailBody.path("data").path("cartItemId").asLong();
        assertThat(cartItemId).isEqualTo(CART_ITEM_ID);

        var duplicate = postJson("/api/v1/users/" + USER_ID + "/cart-items",
                "{\"postId\":" + POST_ID + "}");
        assertThat(duplicate.statusCode()).isEqualTo(409);
        assertThat(objectMapper.readTree(duplicate.body()).path("code").asText())
                .isEqualTo("CART_ITEM_DUPLICATED");

        var folderResponse = postJson("/api/v1/users/" + USER_ID + "/folders/" + FOLDER_ID + "/cart-items",
                "{\"cartItemIds\":[" + cartItemId + "]}");
        assertThat(folderResponse.statusCode()).isEqualTo(200);
        assertThat(jdbcTemplate.queryForObject(
                "select count(*) from folder_items where folder_id = ? and cart_item_id = ?",
                Integer.class, FOLDER_ID, CART_ITEM_ID)).isEqualTo(1);
    }

    /** 사용자가 지정되어도 장바구니에 담지 않았다면 명시적인 null을 반환한다. */
    @Test
    void returnsNullWhenPostIsNotInUsersCart() throws Exception {
        insertPost();
        insertUser(USER_ID);

        var response = getPost("?userId=" + USER_ID);
        var data = objectMapper.readTree(response.body()).path("data");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(data.has("cartItemId")).isTrue();
        assertThat(data.path("cartItemId").isNull()).isTrue();
    }

    /** 같은 자료라도 다른 사용자의 장바구니 항목 ID를 반환하지 않는다. */
    @Test
    void scopesCartItemIdToRequestedUser() throws Exception {
        insertPost();
        insertUser(USER_ID);
        insertUser(OTHER_USER_ID);
        insertCartItem(CART_ITEM_ID, OTHER_USER_ID);

        var response = getPost("?userId=" + USER_ID);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(objectMapper.readTree(response.body()).path("data").path("cartItemId").isNull()).isTrue();

        insertCartItem(CART_ITEM_ID + 1, USER_ID);
        var savedResponse = getPost("?userId=" + USER_ID);
        assertThat(savedResponse.statusCode()).isEqualTo(200);
        assertThat(objectMapper.readTree(savedResponse.body()).path("data").path("cartItemId").asLong())
                .isEqualTo(CART_ITEM_ID + 1);

        var anonymousResponse = getPost();
        assertThat(anonymousResponse.statusCode()).isEqualTo(200);
        assertThat(objectMapper.readTree(anonymousResponse.body()).path("data").path("cartItemId").isNull())
                .isTrue();
    }

    @Test
    void returnsNotFoundForMissingUser() throws Exception {
        insertPost();

        var response = getPost("?userId=" + USER_ID);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(objectMapper.readTree(response.body()).path("code").asText()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    void returnsPostNotFoundWithUserId() throws Exception {
        insertUser(USER_ID);

        var response = getPost("?userId=" + USER_ID);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(objectMapper.readTree(response.body()).path("code").asText()).isEqualTo("POST_NOT_FOUND");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void rejectsNonPositiveUserId(String userId) throws Exception {
        insertPost();

        var response = getPost("?userId=" + userId);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(objectMapper.readTree(response.body()).path("code").asText()).isEqualTo("INVALID_INPUT_VALUE");
    }

    @Test
    void rejectsMalformedUserId() throws Exception {
        var response = getPost("?userId=invalid");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(objectMapper.readTree(response.body()).path("code").asText())
                .isEqualTo("INVALID_PARAMETER_TYPE");
    }

    private void insertUser(long userId) {
        jdbcTemplate.update("insert into users (id, created_at, updated_at) values (?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                userId);
    }

    private void insertCartItem(long cartItemId, long userId) {
        jdbcTemplate.update("insert into cart_items (id, user_id, post_id, created_at) values (?, ?, ?, CURRENT_TIMESTAMP)",
                cartItemId, userId, POST_ID);
    }

    private void insertPost() {
        jdbcTemplate.update("""
                insert into posts (id, category, title, price, created_at, updated_at)
                values (?, 'REPORT', ?, 3000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """, POST_ID, "자료 조회 테스트");
    }

    private HttpResponse<String> getPost() throws Exception {
        return getPost("");
    }

    private HttpResponse<String> getPost(String query) throws Exception {
        String port = environment.getRequiredProperty("local.server.port");
        var request = HttpRequest.newBuilder(
                URI.create("http://127.0.0.1:" + port + "/api/v1/posts/" + POST_ID + query)).GET().build();
        try (var client = HttpClient.newHttpClient()) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    private HttpResponse<String> postJson(String path, String json) throws Exception {
        String port = environment.getRequiredProperty("local.server.port");
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
        try (var client = HttpClient.newHttpClient()) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
