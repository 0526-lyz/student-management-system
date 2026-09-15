-- H2 嵌入式文件数据库：无需显式 CREATE DATABASE / USE，
-- 数据库文件已由 JDBC URL（jdbc:h2:file:./data/student_db）自动创建。
CREATE TABLE IF NOT EXISTS users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(128) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    email      VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    student_no VARCHAR(50) NOT NULL UNIQUE,
    name       VARCHAR(50) NOT NULL,
    gender     VARCHAR(10),
    class_name VARCHAR(50),
    major      VARCHAR(100),
    phone      VARCHAR(20),
    email      VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS grades (
    grade_id    INT AUTO_INCREMENT PRIMARY KEY,
    student_no  VARCHAR(50)  NOT NULL,
    course      VARCHAR(100) NOT NULL,
    score       DECIMAL(5,2) NOT NULL,
    semester    VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL,
    content         VARCHAR(500) NOT NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read         BOOLEAN DEFAULT FALSE
);
