-- Couple module schema (MySQL)

CREATE TABLE IF NOT EXISTS couple_relation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  requester_id BIGINT NOT NULL,
  responder_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  confirm_time DATETIME NULL,
  INDEX idx_couple_relation_requester (requester_id),
  INDEX idx_couple_relation_responder (responder_id),
  INDEX idx_couple_relation_status (status)
);

CREATE TABLE IF NOT EXISTS couple_album (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  relation_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  cover_url VARCHAR(512) NULL,
  cover_delete_hash VARCHAR(128) NULL,
  description VARCHAR(512) NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_couple_album_relation (relation_id)
);

CREATE TABLE IF NOT EXISTS couple_album_photo (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  relation_id BIGINT NOT NULL,
  album_id BIGINT NOT NULL,
  url VARCHAR(512) NOT NULL,
  delete_hash VARCHAR(128) NULL,
  note VARCHAR(512) NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_couple_photo_relation (relation_id),
  INDEX idx_couple_photo_album (album_id)
);

CREATE TABLE IF NOT EXISTS couple_calendar_event (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  relation_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  description VARCHAR(1024) NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NULL,
  shared TINYINT(1) DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_couple_calendar_relation (relation_id),
  INDEX idx_couple_calendar_start (start_time)
);

CREATE TABLE IF NOT EXISTS couple_todo (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  relation_id BIGINT NOT NULL,
  content VARCHAR(512) NOT NULL,
  completed TINYINT(1) DEFAULT 0,
  due_time DATETIME NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_couple_todo_relation (relation_id),
  INDEX idx_couple_todo_completed (completed)
);

CREATE TABLE IF NOT EXISTS couple_message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  relation_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  content VARCHAR(1024) NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_couple_message_relation (relation_id),
  INDEX idx_couple_message_user (user_id)
);

CREATE TABLE IF NOT EXISTS couple_milestone (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  relation_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  description VARCHAR(1024) NULL,
  event_date DATE NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_couple_milestone_relation (relation_id),
  INDEX idx_couple_milestone_date (event_date)
);

CREATE TABLE IF NOT EXISTS couple_important_date (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  relation_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  date DATE NOT NULL,
  remind_days INT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_couple_date_relation (relation_id),
  INDEX idx_couple_date_date (date)
);
