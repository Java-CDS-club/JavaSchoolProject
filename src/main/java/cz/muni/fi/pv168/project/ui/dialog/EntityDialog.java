package cz.muni.fi.pv168.project.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import static javax.swing.JOptionPane.*;

public abstract class EntityDialog<E> {

    private final JPanel panel = new JPanel();
    private int nextComponentRow = 0;
    private final String title;

    EntityDialog(String title) {
        this.title = title;
        panel.setLayout(new GridBagLayout());
    }

    void add(String labelText, JComponent component, boolean required) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = nextComponentRow++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.weightx = 0.0;
        var label = new JLabel(labelText);
        label.setLabelFor(component);
        panel.add(label, c);

        c.gridx++;
        if (required) {
            var requiredLabel = new JLabel("*");
            requiredLabel.setForeground(Color.RED);
            panel.add(requiredLabel, c);
        }

        c.gridx++;
        c.weightx = 1.0;
        panel.add(component, c);
    }

    void add(String labelText, JComponent component) {
        add(labelText, component, false);
    }

    abstract E getEntity();

    abstract void fieldsListener(JButton ok);


    protected JOptionPane getOptionPane(JComponent parent) {
        JOptionPane pane = null;
        if (!(parent instanceof JOptionPane)) {
            pane = getOptionPane((JComponent)parent.getParent());
        } else {
            pane = (JOptionPane) parent;
        }
        return pane;
    }

    public Optional<E> show(JComponent parentComponent) {
        final JButton ok = new JButton("Ok");
        ok.addActionListener(e -> {
            JOptionPane pane = getOptionPane((JComponent)e.getSource());
            pane.setValue(ok);
        });

        ok.setEnabled(false);
        final JButton cancel = new JButton("Cancel");

        cancel.addActionListener(e -> {
            JOptionPane pane = getOptionPane((JComponent)e.getSource());
            pane.setValue(cancel);
        });
        fieldsListener(ok);

        int result = JOptionPane.showOptionDialog(parentComponent, panel, title,
                OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new JButton[]{ok, cancel}, null);
        if (result == OK_OPTION) {
            return Optional.of(getEntity());
        } else {
            return Optional.empty();
        }
    }
}
