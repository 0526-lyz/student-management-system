package com.student.observer;

import com.student.model.Grade;

/**
 * 邮件通知观察者：模拟发送邮件提醒（打印日志）。
 */
public class EmailNotifier implements Observer {

    @Override
    public void update(Grade grade) {
        String warn = grade.getScore() < 60 ? "（不及格，请注意复习）" : "";
        System.out.println("[邮件提醒] 发送邮件给学生[" + grade.getStudentNo() + "]："
                + "您《" + grade.getCourse() + "》的成绩已录入：" + grade.getScore() + " 分" + warn);
    }
}
