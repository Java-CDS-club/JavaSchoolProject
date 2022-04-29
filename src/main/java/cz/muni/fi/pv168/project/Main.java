package cz.muni.fi.pv168.project;

import cz.muni.fi.pv168.project.data.TaskDao;
import cz.muni.fi.pv168.project.data.TaskTypeDao;
import cz.muni.fi.pv168.project.ui.MainWindow;

import cz.muni.fi.pv168.project.data.CustomerDao;
import cz.muni.fi.pv168.project.ui.action.QuitAction;
import cz.muni.fi.pv168.project.ui.error.UncaughtExceptionHandler;
import cz.muni.fi.pv168.project.ui.i18n.I18N;
import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final I18N I18N = new I18N(Main.class);

    public static void main(String[] args) {
        try {
            initNimbusLookAndFeel();
            var dataSource = createDataSource();
            var customerDao = new CustomerDao(dataSource);
            var taskTypeDao = new TaskTypeDao(dataSource);
            var taskDao = new TaskDao(dataSource);
            EventQueue.invokeAndWait(() -> {
                Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler());
                new MainWindow(customerDao, taskTypeDao, taskDao).show();
            });
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private static void showError(Exception ex) {
        EventQueue.invokeLater(() -> {
            ex.printStackTrace();
            Object[] options = {
                    new JButton(new QuitAction()),
            };
            JOptionPane.showOptionDialog(null,
                    I18N.getString("initializationFailedDialogText"),
                    I18N.getString("initializationFailedDialogTitle"),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                    null, options, options[0]);
        });
    }

    private static DataSource createDataSource() {
        String dbPath = System.getProperty("user.home") + "/pv168/db/timesheet-for-freelancer";
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        dataSource.setDatabaseName(dbPath);
        dataSource.setCreateDatabase("create");
        return dataSource;
    }

    private static void initNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Nimbus layout initialization failed", ex);
        }
    }
}
