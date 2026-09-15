package com.student.view;

import com.student.AppContext;
import com.student.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

/**
 * 主菜单界面：根据当前用户角色展示不同的功能入口。
 */
public class MainMenuView extends JFrame {

    private final AppContext context;

    public MainMenuView(AppContext context) {
        this.context = context;
        User user = context.getCurrentUser();
        setTitle("学生信息管理系统 - 主菜单");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        initUI(user);
    }

    private void initUI(User user) {
        setLayout(new BorderLayout());

        JLabel welcome = new JLabel("欢迎，" + user.getUsername() + "（" + roleText(user.getRole()) + "）",
                SwingConstants.CENTER);
        welcome.setFont(welcome.getFont().deriveFont(16f));
        add(welcome, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 10, 10));
        buttons.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        boolean admin = "admin".equals(user.getRole());
        if (admin) {
            addButton(buttons, "学生信息管理", e -> new StudentListView(context, false).setVisible(true));
            addButton(buttons, "成绩管理", e -> new GradeView(context, false).setVisible(true));
            addButton(buttons, "通知消息", e -> new NotificationView(context).setVisible(true));
        } else {
            addButton(buttons, "我的信息", e -> new StudentListView(context, true).setVisible(true));
            addButton(buttons, "我的成绩", e -> new GradeView(context, true).setVisible(true));
            addButton(buttons, "我的通知", e -> new NotificationView(context).setVisible(true));
        }
        addButton(buttons, "退出登录", e -> {
            context.setCurrentUser(null);
            new LoginView(context).setVisible(true);
            dispose();
        });

        add(buttons, BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        panel.add(button);
    }

    private String roleText(String role) {
        return "admin".equals(role) ? "管理员" : "学生";
    }
}
