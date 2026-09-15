package com.student.view;

import com.student.AppContext;
import com.student.model.Grade;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * 成绩管理界面（View 层）。
 * 管理员可录入/修改/删除成绩并多条件查询；学生（readOnly）仅查看自己的成绩。
 * 成绩录入或修改后，会通过观察者模式自动向学生发送通知。
 */
public class GradeView extends JFrame {

    private final AppContext context;
    private final boolean readOnly;

    private final JTextField noField = new JTextField(12);
    private final JTextField courseField = new JTextField(12);
    private final JTextField scoreField = new JTextField(12);
    private final JTextField semesterField = new JTextField(12);
    private final JTextField searchNoField = new JTextField(10);
    private final JTextField searchCourseField = new JTextField(10);

    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable table = new JTable(model);
    private List<Grade> currentList = List.of();

    public GradeView(AppContext context, boolean readOnly) {
        this.context = context;
        this.readOnly = readOnly;
        setTitle(readOnly ? "我的成绩" : "成绩管理");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);
        initUI();
        loadData();
    }

    private void initUI() {
        model.setColumnIdentifiers(new Object[]{"学号", "姓名", "课程", "成绩", "学期", "录入时间"});
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());

        // 顶部：查询栏
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("学号："));
        searchPanel.add(searchNoField);
        searchPanel.add(new JLabel("课程："));
        searchPanel.add(searchCourseField);
        JButton searchButton = new JButton("查询");
        searchButton.addActionListener(e -> doSearch());
        JButton clearButton = new JButton("显示全部");
        clearButton.addActionListener(e -> loadData());
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        // 中部：表单
        JPanel formPanel = new JPanel(new GridLayout(0, 4, 5, 5));
        formPanel.add(new JLabel("学号"));
        formPanel.add(noField);
        formPanel.add(new JLabel("课程"));
        formPanel.add(courseField);
        formPanel.add(new JLabel("成绩"));
        formPanel.add(scoreField);
        formPanel.add(new JLabel("学期"));
        formPanel.add(semesterField);

        JPanel top = new JPanel(new BorderLayout());
        top.add(searchPanel, BorderLayout.NORTH);
        top.add(formPanel, BorderLayout.CENTER);

        // 底部：操作按钮
        JButton addButton = new JButton("录入");
        addButton.addActionListener(e -> doAdd());
        JButton updateButton = new JButton("修改");
        updateButton.addActionListener(e -> doUpdate());
        JButton deleteButton = new JButton("删除");
        deleteButton.addActionListener(e -> doDelete());
        JButton resetButton = new JButton("清空表单");
        resetButton.addActionListener(e -> clearForm());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetButton);

        if (readOnly) {
            setFormEditable(false);
            searchButton.setEnabled(false);
            clearButton.setEnabled(false);
            addButton.setEnabled(false);
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
            resetButton.setEnabled(false);
        }

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        if (readOnly) {
            currentList = context.getGradeService().findByStudentNo(context.getCurrentUser().getUsername());
        } else {
            currentList = context.getGradeService().findAll();
        }
        refreshTable();
    }

    private void doSearch() {
        String studentNo = searchNoField.getText().trim();
        String course = searchCourseField.getText().trim();
        currentList = context.getGradeService().search(studentNo, course);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Grade g : currentList) {
            model.addRow(new Object[]{
                    g.getStudentNo(), g.getStudentName(), g.getCourse(),
                    g.getScore(), g.getSemester(), g.getCreateTime()
            });
        }
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentList.size()) {
            return;
        }
        Grade g = currentList.get(row);
        noField.setText(g.getStudentNo());
        courseField.setText(g.getCourse());
        scoreField.setText(String.valueOf(g.getScore()));
        semesterField.setText(g.getSemester());
    }

    private void doAdd() {
        Grade g = readForm();
        if (g == null) {
            return;
        }
        if (context.getGradeService().addGrade(g)) {
            JOptionPane.showMessageDialog(this, "成绩录入成功，已向学生发送通知", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "学号不存在，成绩录入失败", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentList.size()) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择要修改的成绩", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Grade g = readForm();
        if (g == null) {
            return;
        }
        g.setGradeId(currentList.get(row).getGradeId());
        if (context.getGradeService().updateGrade(g)) {
            JOptionPane.showMessageDialog(this, "修改成功，已向学生发送通知", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "修改失败", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentList.size()) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择要删除的成绩", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "确定删除该条成绩吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        if (context.getGradeService().deleteGrade(currentList.get(row).getGradeId())) {
            JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "删除失败", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Grade readForm() {
        String no = noField.getText().trim();
        String course = courseField.getText().trim();
        String scoreText = scoreField.getText().trim();
        if (no.isEmpty() || course.isEmpty() || scoreText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "学号、课程和成绩不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        double score;
        try {
            score = Double.parseDouble(scoreText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "成绩必须是数字", "提示", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Grade g = new Grade();
        g.setStudentNo(no);
        g.setCourse(course);
        g.setScore(score);
        g.setSemester(semesterField.getText().trim());
        return g;
    }

    private void clearForm() {
        noField.setText("");
        courseField.setText("");
        scoreField.setText("");
        semesterField.setText("");
        table.clearSelection();
    }

    private void setFormEditable(boolean editable) {
        noField.setEditable(editable);
        courseField.setEditable(editable);
        scoreField.setEditable(editable);
        semesterField.setEditable(editable);
    }
}
