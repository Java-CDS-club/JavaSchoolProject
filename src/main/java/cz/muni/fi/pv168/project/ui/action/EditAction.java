package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.ui.i18n.I18N;
import cz.muni.fi.pv168.project.ui.operation.EditOperation;
import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public final class EditAction extends AbstractEntityAction<EditOperation> {

    private static final I18N I18N = new I18N(EditAction.class);

    public EditAction(JTabbedPane tabbedPane) {
        super(EditOperation.class, tabbedPane);
        putValue(NAME, I18N.getString("name"));
        putValue(SMALL_ICON, Icons.EDIT_ICON);
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl E"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getOperation()
                .orElseThrow(() -> new IllegalStateException("Selected tab does not support EditOperation"))
                .edit();
    }
}
