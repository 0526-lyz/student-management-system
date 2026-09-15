package com.student.service;

import com.student.dao.StudentDao;
import com.student.dao.StudentDaoImpl;
import com.student.dao.UserDao;
import com.student.dao.UserDaoImpl;
import com.student.model.Student;
import com.student.model.User;
import com.student.util.PasswordUtil;

import java.util.List;

/**
 * 学生业务逻辑：学生信息的增删改查与多条件检索。
 */
public class StudentService {

    private final StudentDao studentDao;
    private final UserDao userDao;

    public StudentService() {
        this.studentDao = new StudentDaoImpl();
        this.userDao = new UserDaoImpl();
    }

    public List<Student> findAll() {
        return studentDao.findAll();
    }

    public List<Student> search(String keyword) {
        return studentDao.search(keyword);
    }

    public Student findByNo(String studentNo) {
        return studentDao.findByNo(studentNo);
    }

    /**
     * 新增学生，学号已存在则返回 false。
     * 同时自动为学生创建登录账号（用户名 = 学号，默认密码 123456）。
     */
    public boolean addStudent(Student student) {
        if (studentDao.existsByNo(student.getStudentNo())) {
            return false;
        }
        boolean ok = studentDao.addStudent(student);
        if (ok && userDao.findByUsername(student.getStudentNo()) == null) {
            User user = new User();
            user.setUsername(student.getStudentNo());
            user.setPassword(PasswordUtil.hash("123456"));
            user.setRole("student");
            user.setEmail(student.getEmail());
            userDao.addUser(user);
        }
        return ok;
    }

    public boolean updateStudent(Student student) {
        return studentDao.updateStudent(student);
    }

    public boolean deleteStudent(int studentId) {
        return studentDao.deleteStudent(studentId);
    }
}
