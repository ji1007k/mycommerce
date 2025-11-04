-- 일반 사용자 (일반 회원가입)
INSERT INTO users (name, email, password, role, phone_number, provider, provider_id, created_at, updated_at, status)
SELECT '홍길동', 'user@email.com', '$2a$10$dummyhashedpassword1', 'USER', '01012345678', NULL, NULL, NOW(), NOW(), 'ACTIVE'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user@email.com');

-- 관리자
INSERT INTO users (name, email, password, role, phone_number, provider, provider_id, created_at, updated_at, status)
SELECT '관리자', 'admin@email.com', '$2a$10$dummyhashedpassword2', 'ADMIN', '01087654321', NULL, NULL, NOW(), NOW(), 'ACTIVE'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@email.com');

-- 매니저
INSERT INTO users (name, email, password, role, phone_number, provider, provider_id, created_at, updated_at, status)
SELECT '매니저', 'manager@email.com', '$2a$10$dummyhashedpassword3', 'MANAGER', '01011112222', NULL, NULL, NOW(), NOW(), 'ACTIVE'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'manager@email.com');

-- OAuth 사용자 (네이버)
INSERT INTO users (name, email, password, role, phone_number, provider, provider_id, created_at, updated_at, status)
SELECT '김네이버', 'naver@email.com', NULL, 'USER', '01022223333', 'NAVER', 'naver_1234567890', NOW(), NOW(), 'ACTIVE'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'naver@email.com');

-- OAuth 사용자 (카카오)
INSERT INTO users (name, email, password, role, phone_number, provider, provider_id, created_at, updated_at, status)
SELECT '이카카오', 'kakao@email.com', NULL, 'USER', '01033334444', 'KAKAO', 'kakao_9876543210', NOW(), NOW(), 'ACTIVE'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'kakao@email.com');