INSERT INTO user_balances (user_id, balance, created_at, updated_at)
VALUES (1, 100000.00, NOW(), NOW()),
       (2, 50000.00, NOW(), NOW());

INSERT INTO users (username, created_at, updated_at)
VALUES ('john_doe', NOW(), NOW()),
       ('jane_smith', NOW(), NOW());

-- 45명의 사용자 추가
INSERT INTO queues (user_id, queue_position, queue_token, issued_time, status, is_active, activation_time, expiration_time, created_at, updated_at)
VALUES
    (1, 1, 'token001', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (2, 2, 'token002', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (3, 3, 'token003', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (4, 4, 'token004', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (5, 5, 'token005', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (6, 6, 'token006', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (7, 7, 'token007', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (8, 8, 'token008', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (9, 9, 'token009', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (10, 10, 'token010', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (11, 11, 'token011', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (12, 12, 'token012', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (13, 13, 'token013', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (14, 14, 'token014', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (15, 15, 'token015', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (16, 16, 'token016', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (17, 17, 'token017', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (18, 18, 'token018', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (19, 19, 'token019', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (20, 20, 'token020', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (21, 21, 'token021', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (22, 22, 'token022', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (23, 23, 'token023', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (24, 24, 'token024', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (25, 25, 'token025', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (26, 26, 'token026', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (27, 27, 'token027', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (28, 28, 'token028', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (29, 29, 'token029', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (30, 30, 'token030', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (31, 31, 'token031', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (32, 32, 'token032', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (33, 33, 'token033', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (34, 34, 'token034', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (35, 35, 'token035', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (36, 36, 'token036', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (37, 37, 'token037', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (38, 38, 'token038', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (39, 39, 'token039', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (40, 40, 'token040', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (41, 41, 'token041', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (42, 42, 'token042', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (43, 43, 'token043', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (44, 44, 'token044', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW()),
    (45, 45, 'token045', NOW(), 'TOKEN_ISSUED', TRUE, NOW(), DATEADD(MINUTE, 5, NOW()), NOW(), NOW());

INSERT INTO payments (amount, payment_status, payment_time, created_at, updated_at)
VALUES (100000.00, 'COMPLETED', NOW(), NOW(), NOW()),
       (50000.00, 'PENDING', NOW(), NOW(), NOW());

INSERT INTO reservations (payment_id, schedule_id, user_id, seat_id, reservation_status, reservation_time, expiration_time, created_at, updated_at)
VALUES
    (1, 1, 1, 1, 'CONFIRMED', NOW(), DATEADD(DAY, 1, NOW()), NOW(), NOW()),
    (2, 1, 2, 2, 'TEMPORARY', NOW(), DATEADD(DAY, 1, NOW()), NOW(), NOW());

INSERT INTO seats (seat_type_id, schedule_id, seat_number, created_at, updated_at)
VALUES (1, 1, 101, NOW(), NOW()),
       (1, 1, 102, NOW(), NOW());

INSERT INTO seat_types (concert_id, price, seat_type_name, created_at, updated_at)
VALUES (1, 50000.00, 'VIP', NOW(), NOW()),
       (1, 30000.00, 'General', NOW(), NOW());

INSERT INTO concerts (title, poster_url, created_at, updated_at)
VALUES ('Rock Concert', 'http://example.com/poster1.jpg', NOW(), NOW()),
       ('Jazz Concert', 'http://example.com/poster2.jpg', NOW(), NOW());

INSERT INTO concert_schedules (concert_id, date, duration, ticket_start_time, ticket_end_time, created_at, updated_at)
VALUES
    (1, DATEADD(DAY, 7, NOW()), 120, NOW(), DATEADD(HOUR, 1, NOW()), NOW(), NOW()),
    (2, DATEADD(DAY, 14, NOW()), 90, NOW(), DATEADD(HOUR, 1, NOW()), NOW(), NOW());

INSERT INTO concert_images (concert_id, image_url, created_at, updated_at)
VALUES (1, 'http://example.com/image1.jpg', NOW(), NOW()),
       (2, 'http://example.com/image2.jpg', NOW(), NOW());
