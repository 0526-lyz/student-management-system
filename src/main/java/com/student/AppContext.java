package com.student;

import com.student.model.User;
import com.student.service.GradeService;
import com.student.service.NotificationService;
import com.student.service.StudentService;
import com.student.service.UserService;

/**
 * 应用上下文：集中持有各 Service 与当前登录用户，供视图层调用。
 */
public class AppContext {

    private User currentUser;
    private final UserService userService;
    private final StudentService studentService;
    private final GradeService gradeService;
    private final NotificationService notificationService;

    public AppContext() {
        this.userService = new UserService();
        this.studentService = new StudentService();
        this.gradeService = new GradeService();
        this.notificationService = new NotificationService();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public UserService getUserService() {
        return userService;
    }

    public StudentService getStudentService() {
        return studentService;
    }

    public GradeService getGradeService() {
        return gradeService;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }
}
