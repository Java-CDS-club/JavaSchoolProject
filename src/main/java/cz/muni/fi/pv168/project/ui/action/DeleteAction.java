package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.ui.i18n.I18N;
import cz.muni.fi.pv168.project.ui.operation.DeleteOperation;
import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public final class DeleteAction extends AbstractEntityAction<DeleteOperation> {

    private static final I18N I18N = new I18N(DeleteAction.class);

    public DeleteAction(JTabbedPane tabbedPane) {
        super(DeleteOperation.class, tabbedPane);
        putValue(NAME, I18N.getString("name"));
        putValue(SMALL_ICON, Icons.DELETE_ICON);
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl D"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getOperation()
                .orElseThrow(() -> new IllegalStateException("Selected tab does not support DeleteOperation"))
                .delete();
    }
}
