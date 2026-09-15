package com.student.dao;

import com.student.model.Notification;
import com.student.util.DatabaseConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知数据访问实现（JDBC + PreparedStatement）。
 */
public class NotificationDaoImpl implements NotificationDao {

    private final DatabaseConnectionUtil db = DatabaseConnectionUtil.getInstance();

    @Override
    public boolean insert(Notification notification) {
        String sql = "INSERT INTO notifications (username, content, create_time, is_read) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notification.getUsername());
            ps.setString(2, notification.getContent());
            ps.setTimestamp(3, notification.getCreateTime() != null ? notification.getCreateTime() : new Timestamp(System.currentTimeMillis()));
            ps.setBoolean(4, notification.isRead());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("插入通知失败", e);
        }
    }

    @Override
    public List<Notification> findByUsername(String username) {
        String sql = "SELECT * FROM notifications WHERE username = ? ORDER BY create_time DESC";
        List<Notification> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询通知失败", e);
        }
        return list;
    }

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM notifications ORDER BY create_time DESC";
        List<Notification> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询通知失败", e);
        }
        return list;
    }

    @Override
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("标记通知已读失败", e);
        }
    }

    @Override
    public boolean markAllRead(String username) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE username = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("标记全部已读失败", e);
        }
    }

    private Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setNotificationId(rs.getInt("notification_id"));
        n.setUsername(rs.getString("username"));
        n.setContent(rs.getString("content"));
        n.setCreateTime(rs.getTimestamp("create_time"));
        n.setRead(rs.getBoolean("is_read"));
        return n;
    }
}
