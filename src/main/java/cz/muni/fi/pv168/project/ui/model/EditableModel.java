package cz.muni.fi.pv168.project.ui.model;

import java.util.List;
import java.util.stream.Stream;

public interface EditableModel<E> {

    void deleteRows(List<Integer> indexes);

    void updateRows(List<Integer> indexes);

    void addRow(E employee);

    void updateRow(E employee);

    E getEntity(int rowIndex);
}
