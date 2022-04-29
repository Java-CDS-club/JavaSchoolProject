package cz.muni.fi.pv168.project.ui;

import cz.muni.fi.pv168.project.data.CustomerDao;
import cz.muni.fi.pv168.project.data.TaskDao;
import cz.muni.fi.pv168.project.data.TaskEventEmitter;
import cz.muni.fi.pv168.project.data.TaskTypeDao;
import cz.muni.fi.pv168.project.ui.action.*;
import cz.muni.fi.pv168.project.ui.i18n.I18N;
import cz.muni.fi.pv168.project.ui.model.*;

import javax.swing.*;
import java.awt.*;

public class MainWindow {


    private static final I18N I18N = new I18N(MainWindow.class);
    private final JFrame frame;

    private final GlobalActions globalActions;

    public MainWindow(CustomerDao customerDao, TaskTypeDao taskTypeDao, TaskDao taskDao) {
        frame = createFrame();
        setMinimumSize();

        var eventEmitter = new TaskEventEmitter();

        var tabbedPane = new JTabbedPane();

        var taskTypeTableModel = new TaskTypeTableModel(taskTypeDao, eventEmitter);
        var customerTableModel = new CustomerTableModel(customerDao, eventEmitter);
        var taskTableModel = new TaskTableModel(taskDao, eventEmitter);

        var customerListModel = new EntityListModelAdapter<>(customerTableModel);
        var taskTypeListModel = new EntityListModelAdapter<>(taskTypeTableModel);

        globalActions = GlobalActions.builder()
                .addAction(new AddAction(tabbedPane))
                .deleteAction(new DeleteAction(tabbedPane))
                .editAction(new EditAction(tabbedPane))
                .build();


        var taskTablePanel = new TaskTablePanel(taskTableModel, customerListModel, taskTypeListModel, globalActions);
        var customerTablePanel = new CustomerTablePanel(customerTableModel, globalActions, customerDao);
        var taskTypeTablePanel = new TaskTypeTablePanel(taskTypeTableModel, globalActions);

        tabbedPane.add(I18N.getString("taskTab"), taskTablePanel);
        tabbedPane.add(I18N.getString("customerTab"), customerTablePanel);
        tabbedPane.add(I18N.getString("taskTypeTab"), taskTypeTablePanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(createToolbar(), BorderLayout.BEFORE_FIRST_LINE);
        frame.setJMenuBar(createMenuBar());
        frame.pack();
    }

    private void setMinimumSize() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame.setMinimumSize(new Dimension((int) (screenWidth / 1.5), screenHeight / 2));
    }

    public void show() {
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        var newFrame = new JFrame(I18N.getString("title"));
        newFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return newFrame;
    }

    private JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();
        var editMenu = new JMenu(I18N.getString("editMenu"));
        editMenu.setMnemonic('e');
        editMenu.add(globalActions.getAddAction());
        editMenu.add(globalActions.getDeleteAction());
        editMenu.add(globalActions.getEditAction());
        editMenu.addSeparator();
        menuBar.add(editMenu);
        return menuBar;
    }

    private JToolBar createToolbar() {
        var toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(globalActions.getAddAction());
        toolbar.add(globalActions.getDeleteAction());
        toolbar.add(globalActions.getEditAction());
        return toolbar;
    }

}
