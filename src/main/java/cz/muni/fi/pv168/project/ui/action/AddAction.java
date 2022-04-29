package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.ui.i18n.I18N;
import cz.muni.fi.pv168.project.ui.operation.AddOperation;
import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public final class AddAction extends AbstractEntityAction<AddOperation> {

    private static final I18N I18N = new I18N(AddAction.class);

    public AddAction(JTabbedPane tabbedPane) {
        super(AddOperation.class, tabbedPane);
        putValue(NAME, I18N.getString("name"));
        putValue(SMALL_ICON, Icons.ADD_ICON);
        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl N"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getOperation()
                .orElseThrow(() -> new IllegalStateException("Selected tab does not support AddOperation"))
                .add();
    }
}
