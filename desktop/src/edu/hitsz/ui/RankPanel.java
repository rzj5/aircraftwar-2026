package edu.hitsz.ui;

import edu.hitsz.dao.ScoreDao;
import edu.hitsz.dao.ScoreDaoImpl;
import edu.hitsz.entity.ScoreRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 难度隔离的排行榜面板，无空指针问题，适配GameFrame卡片布局
 */
public class RankPanel {
    // 主面板（必须先初始化，否则会空指针）
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JTable rankTable;
    private JButton deleteButton;
    private JButton backButton;

    private final String currentDifficulty;
    private final ScoreDao scoreDao;
    private DefaultTableModel tableModel;

    public RankPanel(String difficulty) {
        this.currentDifficulty = difficulty;
        this.scoreDao = new ScoreDaoImpl(difficulty);

        // ========== 关键修复：先初始化mainPanel，再做其他操作 ==========
        initMainPanel();
        initComponents();
        initTable();
        initButtonEvents();
        refreshTableData();
    }

    /**
     * 初始化主面板（解决空指针的核心）
     */
    private void initMainPanel() {
        // 创建主面板，设置布局和大小，和GameFrame窗口尺寸一致
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(512, 768));
        mainPanel.setBackground(Color.WHITE);
    }

    /**
     * 初始化所有UI组件，并添加到mainPanel中
     */
    private void initComponents() {
        // 1. 标题栏
        titleLabel = new JLabel(currentDifficulty + "难度 得分排行榜", SwingConstants.CENTER);
        titleLabel.setFont(new Font("黑体", Font.BOLD, 24));
        titleLabel.setPreferredSize(new Dimension(512, 60));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 2. 排行榜表格
        rankTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(rankTable);
        scrollPane.setPreferredSize(new Dimension(512, 600));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 3. 按钮栏
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        deleteButton = new JButton("删除选中记录");
        backButton = new JButton("返回主菜单");
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 初始化表格模型
     */
    private void initTable() {
        String[] columnNames = {"名次", "玩家姓名", "分数", "游戏时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rankTable.setModel(tableModel);
        rankTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * 初始化按钮事件
     */
    private void initButtonEvents() {
        deleteButton.addActionListener(e -> deleteSelectedRecord());
        backButton.addActionListener(e -> GameFrame.getInstance().showPanel(GameFrame.MENU_PANEL));
    }

    /**
     * 删除选中记录
     */
    private void deleteSelectedRecord() {
        int selectedRow = rankTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainPanel, "请先选择要删除的记录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int rank = selectedRow + 1;
        int confirm = JOptionPane.showConfirmDialog(mainPanel,
                "确定要删除第" + rank + "名的记录吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            scoreDao.deleteRecord(rank);
            refreshTableData();
            JOptionPane.showMessageDialog(mainPanel, "删除成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 刷新当前难度的排行榜数据
     */
    public void refreshTableData() {
        tableModel.setRowCount(0);
        List<ScoreRecord> records = scoreDao.getAllRecords();
        for (int i = 0; i < records.size(); i++) {
            ScoreRecord record = records.get(i);
            Object[] rowData = {
                    i + 1,
                    record.getUserName(),
                    record.getScore(),
                    record.getFormattedTime()
            };
            tableModel.addRow(rowData);
        }
    }

    /**
     * 返回主面板，供GameFrame卡片布局调用
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
}
