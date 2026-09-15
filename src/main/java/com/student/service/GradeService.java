package com.student.service;

import com.student.dao.GradeDao;
import com.student.dao.GradeDaoImpl;
import com.student.dao.StudentDao;
import com.student.dao.StudentDaoImpl;
import com.student.model.Grade;
import com.student.observer.Observer;
import com.student.observer.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * 成绩业务逻辑（同时充当观察者模式中的“具体主题”）。
 *
 * 当成绩被录入或更新时，通过 notifyObservers 通知所有观察者
 * （如邮件通知、站内信通知），实现成绩发布自动提醒。
 */
public class GradeService implements Subject {

    private final GradeDao gradeDao;
    private final StudentDao studentDao;
    private final List<Observer> observers = new ArrayList<>();

    public GradeService() {
        this.gradeDao = new GradeDaoImpl();
        this.studentDao = new StudentDaoImpl();
    }

    /** 录入成绩：校验学生存在，成功后通知观察者。 */
    public boolean addGrade(Grade grade) {
        if (studentDao.findByNo(grade.getStudentNo()) == null) {
            return false;
        }
        boolean ok = gradeDao.addGrade(grade);
        if (ok) {
            notifyObservers(grade);
        }
        return ok;
    }

    /** 修改成绩：成功后通知观察者。 */
    public boolean updateGrade(Grade grade) {
        boolean ok = gradeDao.updateGrade(grade);
        if (ok) {
            notifyObservers(grade);
        }
        return ok;
    }

    public boolean deleteGrade(int gradeId) {
        return gradeDao.deleteGrade(gradeId);
    }

    public List<Grade> findAll() {
        return gradeDao.findAll();
    }

    public List<Grade> findByStudentNo(String studentNo) {
        return gradeDao.findByStudentNo(studentNo);
    }

    public List<Grade> search(String studentNo, String course) {
        return gradeDao.search(studentNo, course);
    }

    @Override
    public void registerObserver(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Grade grade) {
        for (Observer observer : observers) {
            observer.update(grade);
        }
    }
}
