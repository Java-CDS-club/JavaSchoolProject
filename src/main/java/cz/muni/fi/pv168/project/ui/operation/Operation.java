package cz.muni.fi.pv168.project.ui.operation;

import javax.swing.event.ChangeListener;

public interface Operation {

    boolean isEnabled();

    String getDescription();

    void addOperationEnabledListener(ChangeListener changeListener);

    void removeOperationEnabledListener(ChangeListener changeListener);
}
