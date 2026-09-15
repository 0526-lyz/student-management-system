package com.student.dao;

import com.student.model.Grade;
import com.student.util.DatabaseConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 成绩数据访问实现（JDBC + PreparedStatement）。
 */
public class GradeDaoImpl implements GradeDao {

    private final DatabaseConnectionUtil db = DatabaseConnectionUtil.getInstance();

    private static final String SELECT_SQL =
            "SELECT g.grade_id, g.student_no, s.name AS student_name, g.course, g.score, g.semester, g.create_time "
            + "FROM grades g LEFT JOIN students s ON g.student_no = s.student_no ";

    @Override
    public boolean addGrade(Grade grade) {
        String sql = "INSERT INTO grades (student_no, course, score, semester) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, grade.getStudentNo());
            ps.setString(2, grade.getCourse());
            ps.setDouble(3, grade.getScore());
            ps.setString(4, grade.getSemester());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("新增成绩失败", e);
        }
    }

    @Override
    public boolean updateGrade(Grade grade) {
        String sql = "UPDATE grades SET student_no = ?, course = ?, score = ?, semester = ? WHERE grade_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, grade.getStudentNo());
            ps.setString(2, grade.getCourse());
            ps.setDouble(3, grade.getScore());
            ps.setString(4, grade.getSemester());
            ps.setInt(5, grade.getGradeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("修改成绩失败", e);
        }
    }

    @Override
    public boolean deleteGrade(int gradeId) {
        String sql = "DELETE FROM grades WHERE grade_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gradeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("删除成绩失败", e);
        }
    }

    @Override
    public List<Grade> findAll() {
        String sql = SELECT_SQL + " ORDER BY g.create_time DESC";
        List<Grade> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询成绩列表失败", e);
        }
        return list;
    }

    @Override
    public List<Grade> findByStudentNo(String studentNo) {
        String sql = SELECT_SQL + " WHERE g.student_no = ? ORDER BY g.create_time DESC";
        List<Grade> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询成绩失败", e);
        }
        return list;
    }

    @Override
    public List<Grade> search(String studentNo, String course) {
        StringBuilder sql = new StringBuilder(SELECT_SQL).append(" WHERE 1 = 1");
        List<String> params = new ArrayList<>();
        if (studentNo != null && !studentNo.trim().isEmpty()) {
            sql.append(" AND g.student_no LIKE ?");
            params.add("%" + studentNo.trim() + "%");
        }
        if (course != null && !course.trim().isEmpty()) {
            sql.append(" AND g.course LIKE ?");
            params.add("%" + course.trim() + "%");
        }
        sql.append(" ORDER BY g.create_time DESC");

        List<Grade> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询成绩失败", e);
        }
        return list;
    }

    private Grade map(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setGradeId(rs.getInt("grade_id"));
        g.setStudentNo(rs.getString("student_no"));
        g.setStudentName(rs.getString("student_name"));
        g.setCourse(rs.getString("course"));
        g.setScore(rs.getDouble("score"));
        g.setSemester(rs.getString("semester"));
        g.setCreateTime(rs.getTimestamp("create_time"));
        return g;
    }
}
