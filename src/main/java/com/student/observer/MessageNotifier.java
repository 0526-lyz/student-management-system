package com.student.observer;

import com.student.model.Grade;
import com.student.model.Notification;
import com.student.service.NotificationService;

import java.sql.Timestamp;

/**
 * 站内信通知观察者：向 notifications 表插入一条站内信。
 */
public class MessageNotifier implements Observer {

    private final NotificationService notificationService;

    public MessageNotifier(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void update(Grade grade) {
        String content = "您的《" + grade.getCourse() + "》成绩已录入：" + grade.getScore() + " 分";
        if (grade.getScore() < 60) {
            content += "（不及格，请注意复习）";
        }

        Notification n = new Notification();
        n.setUsername(grade.getStudentNo());
        n.setContent(content);
        n.setCreateTime(new Timestamp(System.currentTimeMillis()));
        n.setRead(false);
        notificationService.insert(n);

        System.out.println("[站内信提醒] 已发送给用户：" + grade.getStudentNo());
    }
}
