package com.student;

import com.student.observer.EmailNotifier;
import com.student.observer.MessageNotifier;
import com.student.view.LoginView;

import javax.swing.SwingUtilities;

/**
 * 程序入口：初始化数据库、注册观察者并启动登录界面。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppContext context = new AppContext();

            // 观察者模式集成：成绩录入/更新时自动提醒学生
            context.getGradeService().registerObserver(new EmailNotifier());
            context.getGradeService().registerObserver(new MessageNotifier(context.getNotificationService()));

            new LoginView(context).setVisible(true);
        });
    }
}
