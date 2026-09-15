package com.student.view;

import com.student.AppContext;
import com.student.model.Student;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
 * 学生信息管理界面（View 层）。
 * 管理员可增删改查；学生（readOnly）仅查看自己的信息。
 */
public class StudentListView extends JFrame {

    private final AppContext context;
    private final boolean readOnly;

    private final JTextField noField = new JTextField(12);
    private final JTextField nameField = new JTextField(12);
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"男", "女"});
    private final JTextField classField = new JTextField(12);
    private final JTextField majorField = new JTextField(12);
    private final JTextField phoneField = new JTextField(12);
    private final JTextField emailField = new JTextField(12);
    private final JTextField searchField = new JTextField(12);

    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable table = new JTable(model);
    private List<Student> currentList = List.of();

    public StudentListView(AppContext context, boolean readOnly) {
        this.context = context;
        this.readOnly = readOnly;
        setTitle(readOnly ? "我的信息" : "学生信息管理");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        initUI();
        loadData();
    }

    private void initUI() {
        model.setColumnIdentifiers(new Object[]{"学号", "姓名", "性别", "班级", "专业", "电话", "邮箱"});
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());

        // 顶部：查询栏 + 表单
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("查询（学号/姓名/班级/专业）："));
        searchPanel.add(searchField);

        JButton searchButton = new JButton("查询");
        searchButton.addActionListener(e -> doSearch());
        JButton clearButton = new JButton("显示全部");
        clearButton.addActionListener(e -> loadData());
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        JPanel formPanel = new JPanel(new GridLayout(0, 4, 5, 5));
        formPanel.add(new JLabel("学号"));
        formPanel.add(noField);
        formPanel.add(new JLabel("姓名"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("性别"));
        formPanel.add(genderBox);
        formPanel.add(new JLabel("班级"));
        formPanel.add(classField);
        formPanel.add(new JLabel("专业"));
        formPanel.add(majorField);
        formPanel.add(new JLabel("电话"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("邮箱"));
        formPanel.add(emailField);

        JPanel top = new JPanel(new BorderLayout());
        top.add(searchPanel, BorderLayout.NORTH);
        top.add(formPanel, BorderLayout.CENTER);

        // 底部：操作按钮
        JButton addButton = new JButton("新增");
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
            Student s = context.getStudentService().findByNo(context.getCurrentUser().getUsername());
            currentList = (s == null) ? List.of() : List.of(s);
        } else {
            currentList = context.getStudentService().findAll();
        }
        refreshTable();
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        currentList = keyword.isEmpty()
                ? context.getStudentService().findAll()
                : context.getStudentService().search(keyword);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Student s : currentList) {
            model.addRow(new Object[]{
                    s.getStudentNo(), s.getName(), s.getGender(),
                    s.getClassName(), s.getMajor(), s.getPhone(), s.getEmail()
            });
        }
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentList.size()) {
            return;
        }
        Student s = currentList.get(row);
        noField.setText(s.getStudentNo());
        nameField.setText(s.getName());
        genderBox.setSelectedItem(s.getGender());
        classField.setText(s.getClassName());
        majorField.setText(s.getMajor());
        phoneField.setText(s.getPhone());
        emailField.setText(s.getEmail());
    }

    private void doAdd() {
        Student s = readForm();
        if (s == null) {
            return;
        }
        if (context.getStudentService().addStudent(s)) {
            JOptionPane.showMessageDialog(this, "新增成功（已自动创建登录账号，默认密码 123456）",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "学号已存在，新增失败", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentList.size()) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择要修改的学生", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Student s = readForm();
        if (s == null) {
            return;
        }
        s.setStudentId(currentList.get(row).getStudentId());
        if (context.getStudentService().updateStudent(s)) {
            JOptionPane.showMessageDialog(this, "修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "修改失败", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentList.size()) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择要删除的学生", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "确定删除该学生吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        if (context.getStudentService().deleteStudent(currentList.get(row).getStudentId())) {
            JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "删除失败", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Student readForm() {
        String no = noField.getText().trim();
        String name = nameField.getText().trim();
        if (no.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "学号和姓名不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Student s = new Student();
        s.setStudentNo(no);
        s.setName(name);
        s.setGender((String) genderBox.getSelectedItem());
        s.setClassName(classField.getText().trim());
        s.setMajor(majorField.getText().trim());
        s.setPhone(phoneField.getText().trim());
        s.setEmail(emailField.getText().trim());
        return s;
    }

    private void clearForm() {
        noField.setText("");
        nameField.setText("");
        genderBox.setSelectedIndex(0);
        classField.setText("");
        majorField.setText("");
        phoneField.setText("");
        emailField.setText("");
        table.clearSelection();
    }

    private void setFormEditable(boolean editable) {
        noField.setEditable(editable);
        nameField.setEditable(editable);
        genderBox.setEnabled(editable);
        classField.setEditable(editable);
        majorField.setEditable(editable);
        phoneField.setEditable(editable);
        emailField.setEditable(editable);
    }
}
