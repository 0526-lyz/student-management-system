package com.student.service;

import com.student.dao.NotificationDao;
import com.student.dao.NotificationDaoImpl;
import com.student.model.Notification;

import java.util.List;

/**
 * 通知业务逻辑：站内信的写入、查询与已读标记。
 */
public class NotificationService {

    private final NotificationDao notificationDao;

    public NotificationService() {
        this.notificationDao = new NotificationDaoImpl();
    }

    public boolean insert(Notification notification) {
        return notificationDao.insert(notification);
    }

    public List<Notification> findByUsername(String username) {
        return notificationDao.findByUsername(username);
    }

    public List<Notification> findAll() {
        return notificationDao.findAll();
    }

    public boolean markAsRead(int notificationId) {
        return notificationDao.markAsRead(notificationId);
    }

    public boolean markAllRead(String username) {
        return notificationDao.markAllRead(username);
    }
}
