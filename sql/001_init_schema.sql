/*
 HappyCampus 초기 테이블 생성 스크립트
 실행 대상: happycampus 데이터 베이스
 시간 필드는 애플리케이션의 JPA Auditing 에서 설정
 */
USE happycampus;

/*
  사용자 정보.
  UserEntity 및 공통 시간 Entity 기준.
 */
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id)
);

/*
  자료 정보
  PostEntity 및 공통 시간 Entity 기준
 */

CREATE TABLE posts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category VARCHAR(30) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price INT,
    cover_image_url VARCHAR(255),
    saved_year INT,
    file_type VARCHAR(30),
    page_count INT,
    table_of_contents TEXT,
    content TEXT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id)
);

/*
 * 자료에 연결된 이미지 정보.
 */
CREATE TABLE image_url (
    id BIGINT NOT NULL AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_image_url_post FOREIGN KEY (post_id) REFERENCES posts (id)
);

/*
 * 사용자 장바구니 자료.
 * 동일 사용자의 동일 자료 중복 담기를 제한한다.
 */
CREATE TABLE cart_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_cart_items_user_post
        UNIQUE (user_id, post_id),

    CONSTRAINT fk_cart_items_user
        FOREIGN KEY (user_id) REFERENCES users (id),

    CONSTRAINT fk_cart_items_post
        FOREIGN KEY (post_id) REFERENCES posts (id)
);

/*
 * 사용자 폴더 정보.
 * 동일 사용자 내 폴더 이름 중복을 제한한다.
 */
CREATE TABLE folders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_folders_user_name
        UNIQUE (user_id, name),

    CONSTRAINT fk_folders_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);

/*
 * 폴더에 포함된 장바구니 자료.
 * FolderItemEntity에 따라 하나의 장바구니 항목은 하나의 폴더에만 포함된다.
 * 실행 전 cart_items 테이블이 먼저 생성되어야 한다.
 */
CREATE TABLE folder_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    folder_id BIGINT NOT NULL,
    cart_item_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_folder_items_cart_item
        UNIQUE (cart_item_id),

    CONSTRAINT fk_folder_items_folder
        FOREIGN KEY (folder_id) REFERENCES folders (id),

    CONSTRAINT fk_folder_items_cart_item
        FOREIGN KEY (cart_item_id) REFERENCES cart_items (id)
);
