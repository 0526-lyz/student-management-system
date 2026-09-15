package com.student.dao;

import com.student.model.Student;

import java.util.List;

/**
 * 学生数据访问接口（DAO 模式）。
 */
public interface StudentDao {

    List<Student> findAll();

    List<Student> search(String keyword);

    Student findByNo(String studentNo);

    Student findById(int studentId);

    boolean existsByNo(String studentNo);

    boolean addStudent(Student student);

    boolean updateStudent(Student student);

    boolean deleteStudent(int studentId);
}
