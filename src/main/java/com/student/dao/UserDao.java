package com.student.dao;

import com.student.model.User;

import java.util.List;

/**
 * 用户数据访问接口（DAO 模式）。
 */
public interface UserDao {

    User findByUsername(String username);

    User findById(int userId);

    boolean addUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(int userId);

    List<User> findAll();
}
