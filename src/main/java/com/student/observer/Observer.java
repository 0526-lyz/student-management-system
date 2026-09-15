package com.student.observer;

import com.student.model.Grade;

/**
 * 观察者接口：当被观察的主题（成绩服务）发生成绩录入/更新时，通知观察者。
 */
public interface Observer {

    void update(Grade grade);
}
