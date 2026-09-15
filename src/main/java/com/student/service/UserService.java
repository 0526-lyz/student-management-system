package com.student.service;

import com.student.dao.UserDao;
import com.student.dao.UserDaoImpl;
import com.student.model.User;
import com.student.util.PasswordUtil;

import java.util.List;

/**
 * 用户业务逻辑：登录验证、注册、信息维护。
 */
public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    /** 登录验证，成功返回用户，失败返回 null。 */
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (user.getPassword().equals(PasswordUtil.hash(password))) {
            return user;
        }
        return null;
    }

    /** 注册，用户名已存在则返回 false。 */
    public boolean register(String username, String password, String role, String email) {
        if (userDao.findByUsername(username) != null) {
            return false;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hash(password));
        user.setRole(role);
        user.setEmail(email);
        return userDao.addUser(user);
    }

    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }

    public boolean deleteUser(int userId) {
        return userDao.deleteUser(userId);
    }
}
