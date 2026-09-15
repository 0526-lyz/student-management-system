package com.student.dao;

import com.student.model.Grade;

import java.util.List;

/**
 * 成绩数据访问接口（DAO 模式）。
 */
public interface GradeDao {

    boolean addGrade(Grade grade);

    boolean updateGrade(Grade grade);

    boolean deleteGrade(int gradeId);

    List<Grade> findAll();

    List<Grade> findByStudentNo(String studentNo);

    List<Grade> search(String studentNo, String course);
}
