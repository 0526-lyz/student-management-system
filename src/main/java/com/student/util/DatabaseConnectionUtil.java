package com.student.util;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接工具类（单例模式）。
 *
 * 使用 H2 文件数据库，数据保存在 ./data/student_db，开箱即用，
 * 无需单独安装 MySQL 等外部数据库服务。首次启动会自动建表并写入演示数据。
 */
public final class DatabaseConnectionUtil {

    private static final String JDBC_URL = "jdbc:h2:file:./data/student_db;AUTO_SERVER=TRUE";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static volatile DatabaseConnectionUtil instance;

    private DatabaseConnectionUtil() {
        ensureDataDirectory();
        initSchema();
        seedData();
    }

    /** 双重校验锁获取单例。 */
    public static DatabaseConnectionUtil getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionUtil.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionUtil();
                }
            }
        }
        return instance;
    }

    /** 每次调用返回一个新的数据库连接。 */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    private void ensureDataDirectory() {
        File dir = new File("./data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private void initSchema() {
        try (InputStream in = getClass().getResourceAsStream("/schema.sql");
             Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            if (in == null) {
                throw new IllegalStateException("找不到 schema.sql 资源文件");
            }
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (String statement : sql.split(";")) {
                String s = statement.trim();
                if (!s.isEmpty()) {
                    stmt.execute(s);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化数据库表结构失败", e);
        }
    }

    private void seedData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            rs.next();
            if (rs.getInt(1) > 0) {
                return; // 已初始化，跳过
            }
        } catch (SQLException e) {
            throw new RuntimeException("检查用户表失败", e);
        }

        try (Connection conn = getConnection()) {
            // 默认管理员：admin / admin123
            insertUser(conn, "admin", "admin123", "admin", "admin@example.com");

            // 演示学生账号（学号即用户名，默认密码 123456）
            insertStudent(conn, "2021001", "张三", "男", "计算机2101班", "计算机科学与技术", "13800000001", "zhangsan@example.com");
            insertStudent(conn, "2021002", "李四", "女", "软件2101班", "软件工程", "13800000002", "lisi@example.com");
            insertStudent(conn, "2021003", "王五", "男", "计算机2102班", "计算机科学与技术", "13800000003", "wangwu@example.com");

            // 演示成绩数据
            insertGrade(conn, "2021001", "数据结构", 88, "2024-2025-1");
            insertGrade(conn, "2021001", "操作系统", 55, "2024-2025-1");
            insertGrade(conn, "2021002", "数据结构", 92, "2024-2025-1");
            insertGrade(conn, "2021003", "计算机网络", 73, "2024-2025-1");
        } catch (SQLException e) {
            throw new RuntimeException("初始化演示数据失败", e);
        }
    }

    private void insertUser(Connection conn, String username, String plainPassword, String role, String email) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(plainPassword));
            ps.setString(3, role);
            ps.setString(4, email);
            ps.executeUpdate();
        }
    }

    private void insertStudent(Connection conn, String no, String name, String gender,
                               String className, String major, String phone, String email) throws SQLException {
        String sql = "INSERT INTO students (student_no, name, gender, class_name, major, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, no);
            ps.setString(2, name);
            ps.setString(3, gender);
            ps.setString(4, className);
            ps.setString(5, major);
            ps.setString(6, phone);
            ps.setString(7, email);
            ps.executeUpdate();
        }
        // 同步创建学生登录账号
        insertUser(conn, no, "123456", "student", email);
    }

    private void insertGrade(Connection conn, String studentNo, String course, double score, String semester) throws SQLException {
        String sql = "INSERT INTO grades (student_no, course, score, semester) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            ps.setString(2, course);
            ps.setDouble(3, score);
            ps.setString(4, semester);
            ps.executeUpdate();
        }
    }
}
