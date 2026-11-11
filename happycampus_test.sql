
USE happycampus;

-- 동일 이름의 프로시저가 이미 존재한다면
-- 기존 프로시저를 덮어쓰지 않고 오류가 발생한다.
DELIMITER $$

CREATE PROCEDURE seed_happycampus_cart_test_20261112()
BEGIN
    DECLARE v_prefix VARCHAR(80);
    DECLARE v_user_a BIGINT;
    DECLARE v_user_b BIGINT;
    DECLARE v_folder_f1 BIGINT;
    DECLARE v_folder_f2 BIGINT;
    DECLARE v_folder_b BIGINT;
    DECLARE v_seq INT DEFAULT 1;
    DECLARE v_category VARCHAR(30);
    DECLARE v_post_id BIGINT;
    DECLARE v_count INT;
    DECLARE v_now DATETIME(6);

    -- 오류 발생 시 전체 작업 취소
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

    SET v_prefix = CONCAT(
        'HC_TEST_',
        REPLACE(UUID(), '-', ''),
        '_'
    );
    SET v_now = NOW(6);

START TRANSACTION;

-- =========================================
-- 1. 사용자 A, B 생성
-- =========================================

INSERT INTO users (created_at, updated_at)
VALUES (v_now, v_now);

SET v_user_a = LAST_INSERT_ID();

INSERT INTO users (created_at, updated_at)
VALUES (v_now, v_now);

SET v_user_b = LAST_INSERT_ID();

    -- =========================================
    -- 2. 게시물 23개 생성
    -- 01~12: REPORT
    -- 13~23: THESIS
    -- =========================================

    WHILE v_seq <= 23 DO

        IF v_seq <= 12 THEN
            SET v_category = 'REPORT';
ELSE
            SET v_category = 'THESIS';
END IF;

INSERT INTO posts (
    created_at,
    updated_at,
    category,
    title,
    content,
    cover_image_url,
    description,
    file_type,
    page_count,
    price,
    saved_year,
    table_of_contents
)
VALUES (
           v_now,
           v_now,
           v_category,
           CONCAT(
                   v_prefix,
                   'POST_',
                   LPAD(v_seq, 2, '0')
           ),
           CONCAT('테스트 본문 ', v_seq),
           CONCAT(
                   'https://example.com/test/',
                   v_seq,
                   '.jpg'
           ),
           CONCAT('장바구니 조회 테스트 자료 ', v_seq),
           CASE
               WHEN MOD(v_seq, 2) = 0 THEN 'PDF'
               ELSE 'DOCX'
               END,
           10 + v_seq,
           1000 + v_seq * 500,
           2020 + MOD(v_seq, 7),
           CONCAT(
                   '1. 개요\n2. 본문\n3. 결론 - ',
                   v_seq
           )
       );

SET v_seq = v_seq + 1;
END WHILE;

    -- =========================================
    -- 3. 사용자 A 장바구니 23개
    -- created_at을 1분씩 다르게 설정
    -- =========================================

INSERT INTO cart_items (
    created_at,
    user_id,
    post_id
)
SELECT
    DATE_ADD(
            v_now,
            INTERVAL (
            CAST(
                SUBSTRING(
                    p.title,
                    CHAR_LENGTH(v_prefix) + 6,
                    2
                ) AS SIGNED
            ) - 24
        ) MINUTE
    ),
    v_user_a,
    p.id
FROM posts p
WHERE LEFT(p.title, CHAR_LENGTH(v_prefix)) = v_prefix;

-- =========================================
-- 4. 사용자 B 장바구니 2개
-- A와 동일한 게시물 01, 13 사용
-- =========================================

INSERT INTO cart_items (
    created_at,
    user_id,
    post_id
)
SELECT
    v_now,
    v_user_b,
    p.id
FROM posts p
WHERE p.title IN (
                  CONCAT(v_prefix, 'POST_01'),
                  CONCAT(v_prefix, 'POST_13')
    );

-- =========================================
-- 5. 폴더 생성
-- =========================================

INSERT INTO folders (
    created_at,
    updated_at,
    user_id,
    name
)
VALUES (
           v_now,
           v_now,
           v_user_a,
           CONCAT(v_prefix, 'F1')
       );

SET v_folder_f1 = LAST_INSERT_ID();

INSERT INTO folders (
    created_at,
    updated_at,
    user_id,
    name
)
VALUES (
           v_now,
           v_now,
           v_user_a,
           CONCAT(v_prefix, 'F2')
       );

SET v_folder_f2 = LAST_INSERT_ID();

INSERT INTO folders (
    created_at,
    updated_at,
    user_id,
    name
)
VALUES (
           v_now,
           v_now,
           v_user_b,
           CONCAT(v_prefix, 'B_FOLDER')
       );

SET v_folder_b = LAST_INSERT_ID();

    -- =========================================
    -- 6. A의 F1: REPORT 7 + THESIS 3
    -- POST_01~07, POST_13~15
    -- =========================================

INSERT INTO folder_items (
    created_at,
    folder_id,
    cart_item_id
)
SELECT
    v_now,
    v_folder_f1,
    ci.id
FROM cart_items ci
         JOIN posts p ON p.id = ci.post_id
WHERE ci.user_id = v_user_a
  AND (
    p.title IN (
                CONCAT(v_prefix, 'POST_01'),
                CONCAT(v_prefix, 'POST_02'),
                CONCAT(v_prefix, 'POST_03'),
                CONCAT(v_prefix, 'POST_04'),
                CONCAT(v_prefix, 'POST_05'),
                CONCAT(v_prefix, 'POST_06'),
                CONCAT(v_prefix, 'POST_07')
        )
        OR p.title IN (
                       CONCAT(v_prefix, 'POST_13'),
                       CONCAT(v_prefix, 'POST_14'),
                       CONCAT(v_prefix, 'POST_15')
        )
    );

-- =========================================
-- 7. A의 F2: REPORT 4 + THESIS 5
-- POST_08~11, POST_16~20
-- =========================================

INSERT INTO folder_items (
    created_at,
    folder_id,
    cart_item_id
)
SELECT
    v_now,
    v_folder_f2,
    ci.id
FROM cart_items ci
         JOIN posts p ON p.id = ci.post_id
WHERE ci.user_id = v_user_a
  AND p.title IN (
                  CONCAT(v_prefix, 'POST_08'),
                  CONCAT(v_prefix, 'POST_09'),
                  CONCAT(v_prefix, 'POST_10'),
                  CONCAT(v_prefix, 'POST_11'),
                  CONCAT(v_prefix, 'POST_16'),
                  CONCAT(v_prefix, 'POST_17'),
                  CONCAT(v_prefix, 'POST_18'),
                  CONCAT(v_prefix, 'POST_19'),
                  CONCAT(v_prefix, 'POST_20')
    );

-- POST_12, POST_21~23은 폴더 미지정

-- =========================================
-- 8. B의 폴더: 장바구니 2개
-- =========================================

INSERT INTO folder_items (
    created_at,
    folder_id,
    cart_item_id
)
SELECT
    v_now,
    v_folder_b,
    ci.id
FROM cart_items ci
WHERE ci.user_id = v_user_b;

-- =========================================
-- 9. 트랜잭션 내부 건수 검증
-- =========================================

SELECT COUNT(*) INTO v_count
FROM cart_items
WHERE user_id = v_user_a;

IF v_count <> 23 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A cart count mismatch';
END IF;

SELECT COUNT(*) INTO v_count
FROM cart_items
WHERE user_id = v_user_b;

IF v_count <> 2 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'B cart count mismatch';
END IF;

SELECT COUNT(*) INTO v_count
FROM cart_items ci
         JOIN posts p ON p.id = ci.post_id
WHERE ci.user_id = v_user_a
  AND p.category = 'REPORT';

IF v_count <> 12 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A REPORT count mismatch';
END IF;

SELECT COUNT(*) INTO v_count
FROM folder_items
WHERE folder_id = v_folder_f1;

IF v_count <> 10 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'F1 count mismatch';
END IF;

SELECT COUNT(*) INTO v_count
FROM folder_items fi
         JOIN cart_items ci ON ci.id = fi.cart_item_id
         JOIN posts p ON p.id = ci.post_id
WHERE fi.folder_id = v_folder_f1
  AND p.category = 'REPORT';

IF v_count <> 7 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'F1 REPORT count mismatch';
END IF;

SELECT COUNT(*) INTO v_count
FROM folder_items
WHERE folder_id = v_folder_f2;

IF v_count <> 9 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'F2 count mismatch';
END IF;

SELECT COUNT(*) INTO v_count
FROM folder_items
WHERE folder_id = v_folder_b;

IF v_count <> 2 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'B folder count mismatch';
END IF;

    -- 모든 검증 성공 시에만 커밋
COMMIT;

-- =========================================
-- 10. 생성된 ID 출력
-- =========================================

SELECT
    v_prefix AS test_prefix,
    v_user_a AS user_a_id,
    v_user_b AS user_b_id,
    v_folder_f1 AS folder_f1_id,
    v_folder_f2 AS folder_f2_id,
    v_folder_b AS folder_b_id;

-- =========================================
-- 11. 테스트 건수 확인
-- =========================================

SELECT 'A_ALL' AS test_case, COUNT(*) AS actual
FROM cart_items
WHERE user_id = v_user_a

UNION ALL

SELECT 'A_REPORT', COUNT(*)
FROM cart_items ci
         JOIN posts p ON p.id = ci.post_id
WHERE ci.user_id = v_user_a
  AND p.category = 'REPORT'

UNION ALL

SELECT 'A_F1', COUNT(*)
FROM folder_items
WHERE folder_id = v_folder_f1

UNION ALL

SELECT 'A_F1_REPORT', COUNT(*)
FROM folder_items fi
         JOIN cart_items ci ON ci.id = fi.cart_item_id
         JOIN posts p ON p.id = ci.post_id
WHERE fi.folder_id = v_folder_f1
  AND p.category = 'REPORT'

UNION ALL

SELECT 'A_F2', COUNT(*)
FROM folder_items
WHERE folder_id = v_folder_f2

UNION ALL

SELECT 'A_NO_FOLDER', COUNT(*)
FROM cart_items ci
WHERE ci.user_id = v_user_a
  AND NOT EXISTS (
    SELECT 1
    FROM folder_items fi
    WHERE fi.cart_item_id = ci.id
)

UNION ALL

SELECT 'B_ALL', COUNT(*)
FROM cart_items
WHERE user_id = v_user_b

UNION ALL

SELECT 'A_WITH_B_FOLDER', COUNT(*)
FROM cart_items ci
WHERE ci.user_id = v_user_a
  AND EXISTS (
    SELECT 1
    FROM folder_items fi
    WHERE fi.cart_item_id = ci.id
      AND fi.folder_id = v_folder_b
);

END$$

DELIMITER ;

-- 테스트 데이터 생성 및 검증 실행
CALL seed_happycampus_cart_test_20261112();

-- 사용한 프로시저 정리
DROP PROCEDURE seed_happycampus_cart_test_20261112;
