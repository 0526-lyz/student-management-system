package com.student.observer;

import com.student.model.Grade;

/**
 * 主题接口：负责注册、移除观察者，并广播通知。
 */
public interface Subject {

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(Grade grade);
}
