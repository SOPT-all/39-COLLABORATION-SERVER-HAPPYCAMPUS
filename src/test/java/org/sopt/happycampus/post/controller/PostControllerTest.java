package org.sopt.happycampus.post.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    @AfterEach
    void clearTestPost() {
        jdbcTemplate.update("delete from image_url where post_id = ?", POST_ID);
        jdbcTemplate.update("delete from posts where id = ?", POST_ID);
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

    private void insertPost() {
        jdbcTemplate.update("""
                insert into posts (id, category, title, price, created_at, updated_at)
                values (?, 'REPORT', ?, 3000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """, POST_ID, "자료 조회 테스트");
    }

    private HttpResponse<String> getPost() throws Exception {
        String port = environment.getRequiredProperty("local.server.port");
        var request = HttpRequest.newBuilder(
                URI.create("http://127.0.0.1:" + port + "/api/v1/posts/" + POST_ID)).GET().build();
        try (var client = HttpClient.newHttpClient()) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
