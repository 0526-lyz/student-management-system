package com.student.dao;

import com.student.model.Student;
import com.student.util.DatabaseConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生数据访问实现（JDBC + PreparedStatement）。
 */
public class StudentDaoImpl implements StudentDao {

    private final DatabaseConnectionUtil db = DatabaseConnectionUtil.getInstance();

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM students ORDER BY student_no";
        List<Student> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询学生列表失败", e);
        }
        return list;
    }

    @Override
    public List<Student> search(String keyword) {
        String sql = "SELECT * FROM students WHERE student_no LIKE ? OR name LIKE ? OR class_name LIKE ? OR major LIKE ? ORDER BY student_no";
        List<Student> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询学生失败", e);
        }
        return list;
    }

    @Override
    public Student findByNo(String studentNo) {
        String sql = "SELECT * FROM students WHERE student_no = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询学生失败", e);
        }
        return null;
    }

    @Override
    public Student findById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询学生失败", e);
        }
        return null;
    }

    @Override
    public boolean existsByNo(String studentNo) {
        String sql = "SELECT COUNT(*) FROM students WHERE student_no = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("检查学号是否存在失败", e);
        }
    }

    @Override
    public boolean addStudent(Student s) {
        String sql = "INSERT INTO students (student_no, name, gender, class_name, major, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentNo());
            ps.setString(2, s.getName());
            ps.setString(3, s.getGender());
            ps.setString(4, s.getClassName());
            ps.setString(5, s.getMajor());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("新增学生失败", e);
        }
    }

    @Override
    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET student_no = ?, name = ?, gender = ?, class_name = ?, major = ?, phone = ?, email = ? WHERE student_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentNo());
            ps.setString(2, s.getName());
            ps.setString(3, s.getGender());
            ps.setString(4, s.getClassName());
            ps.setString(5, s.getMajor());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getEmail());
            ps.setInt(8, s.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("修改学生失败", e);
        }
    }

    @Override
    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("删除学生失败", e);
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setStudentNo(rs.getString("student_no"));
        s.setName(rs.getString("name"));
        s.setGender(rs.getString("gender"));
        s.setClassName(rs.getString("class_name"));
        s.setMajor(rs.getString("major"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        return s;
    }
}
