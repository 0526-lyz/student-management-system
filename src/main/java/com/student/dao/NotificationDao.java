package com.student.dao;

import com.student.model.Notification;

import java.util.List;

/**
 * 通知数据访问接口（DAO 模式）。
 */
public interface NotificationDao {

    boolean insert(Notification notification);

    List<Notification> findByUsername(String username);

    List<Notification> findAll();

    boolean markAsRead(int notificationId);

    boolean markAllRead(String username);
}
