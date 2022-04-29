package cz.muni.fi.pv168.project.ui.error;

import cz.muni.fi.pv168.project.ui.i18n.I18N;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorDialog {

    private static final I18N I18N = new I18N(ErrorDialog.class);

    private final JPanel panel = new JPanel();

    private ErrorDialog(String message) {
        panel.setLayout(new GridBagLayout());
        var constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        var messageLabel = new JLabel(message);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD));
        panel.add(messageLabel, constraints);
    }

    private void show(Component parentComponent) {
        JOptionPane.showMessageDialog(parentComponent,
                panel,
                I18N.getString("title"),
                JOptionPane.ERROR_MESSAGE);
    }

    public static void show(String message) {
        show(message, null);
    }

    public static void show(String message, Component parentComponent) {
        new ErrorDialog(message).show(parentComponent);
    }

}
