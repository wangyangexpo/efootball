-- Create database
CREATE DATABASE IF NOT EXISTS springboot_demo DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE springboot_demo;

-- Create user table
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key',
    username VARCHAR(50) NOT NULL COMMENT 'Username',
    email VARCHAR(100) COMMENT 'Email address',
    status INT DEFAULT 1 COMMENT 'Status: 1-active, 0-inactive',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted INT DEFAULT 0 COMMENT 'Logical delete flag: 0-not deleted, 1-deleted'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User table';

-- Insert demo data
INSERT INTO user (username, email, status) VALUES ('demo_user', 'demo@example.com', 1);
INSERT INTO user (username, email, status) VALUES ('test_user', 'test@example.com', 1);