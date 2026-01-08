USE tennis_db;

CREATE TABLE IF NOT EXISTS users(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    user_type ENUM('ADMIN', 'PLAYER') NOT NULL,
    ranking_points INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME
)CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS courts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    court_number INT NOT NULL,
    surface_type ENUM('CLAY', 'GRASS', 'HARD', 'CARPET') NOT NULL,
    has_roof BOOLEAN DEFAULT FALSE,
    location VARCHAR(255) NOT NULL,
    image_url VARCHAR(500),
    available_for_reservations BOOLEAN DEFAULT TRUE,
    price_per_hour DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    court_id BIGINT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status ENUM('ACTIVE','CANCELLED', 'COMPLETED') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (court_id) REFERENCES courts(id) ON DELETE SET NULL,
    CONSTRAINT uq_court_slot UNIQUE (court_id, start_time, end_time)
) CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payments(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT,
    amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL,
    payment_date DATETIME,
    transaction_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
) CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tournaments(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    start_date          DATETIME,
    end_date            DATETIME,
    `rank`              ENUM ('TIER_3', 'TIER_2', 'TIER_1') NOT NULL,
    entry_fee           DOUBLE,
    ranking_requirement INT,
    status              ENUM ('DRAFT','CANCELLED', 'COMPLETED', 'ONGOING', 'REGISTRATION_CLOSED', 'REGISTRATION_OPEN') not null
) CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tournament_participants(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tournament_id BIGINT NOT NULL,
    FOREIGN KEY(tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES users(id)
) CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL,
    player1_id BIGINT,
    player2_id BIGINT,
    winner_id BIGINT,
    next_match_id BIGINT,
    sets JSON,
    points INT,
    court_id BIGINT,
    scheduled_time DATETIME,
    p1_sets_won INT,
    p2_sets_won INT
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    FOREIGN KEY (winner_id) REFERENCES users(id),
    FOREIGN KEY (player1_id) REFERENCES users(id),
    FOREIGN KEY (player2_id) REFERENCES users(id),
    FOREIGN KEY (next_match_id) REFERENCES matches(id),
    FOREIGN KEY (court_id) REFERENCES courts(id) ON DELETE SET NULL
    ) CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
