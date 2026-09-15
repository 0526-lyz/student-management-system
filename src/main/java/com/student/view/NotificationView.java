package com.student.view;

import com.student.AppContext;
import com.student.model.Notification;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

/**
 * 通知消息界面（View 层）。
 * 学生查看自己的站内信；管理员可查看所有通知。
 */
public class NotificationView extends JFrame {

    private final AppContext context;

    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable table = new JTable(model);
    private List<Notification> currentList = List.of();

    public NotificationView(AppContext context) {
        this.context = context;
        setTitle("通知消息");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(780, 500);
        setLocationRelativeTo(null);
        initUI();
        loadData();
    }

    private void initUI() {
        model.setColumnIdentifiers(new Object[]{"ID", "用户名", "内容", "时间", "已读"});
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton readButton = new JButton("标记已读");
        readButton.addActionListener(e -> doMarkRead());
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadData());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(readButton);
        buttonPanel.add(refreshButton);

        boolean admin = "admin".equals(context.getCurrentUser().getRole());
        if (!admin) {
            JButton allReadButton = new JButton("全部已读");
            allReadButton.addActionListener(e -> doMarkAllRead());
            buttonPanel.add(allReadButton);
        }

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        boolean admin = "admin".equals(context.getCurrentUser().getRole());
        currentList = admin
                ? context.getNotificationService().findAll()
                : context.getNotificationService().findByUsername(context.getCurrentUser().getUsername());
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Notification n : currentList) {
            model.addRow(new Object[]{
                    n.getNotificationId(), n.getUsername(), n.getContent(),
                    n.getCreateTime(), n.isRead() ? "是" : "否"
            });
        }
    }

    private void doMarkRead() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentList.size()) {
            JOptionPane.showMessageDialog(this, "请先选择一条通知", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Notification n = currentList.get(row);
        if (context.getNotificationService().markAsRead(n.getNotificationId())) {
            loadData();
        }
    }

    private void doMarkAllRead() {
        context.getNotificationService().markAllRead(context.getCurrentUser().getUsername());
        loadData();
    }
}
