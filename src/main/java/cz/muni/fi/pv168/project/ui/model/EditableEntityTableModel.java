package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.data.DataAccessObject;
import cz.muni.fi.pv168.project.ui.error.ErrorDialog;
import cz.muni.fi.pv168.project.ui.i18n.I18N;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditableEntityTableModel<E> extends AbstractEntityTableModel<E> implements EditableModel<E> {

    protected static final I18N I18N = new I18N(EditableEntityTableModel.class);

    protected final List<E> rows;
    protected final DataAccessObject<E> dataAccessObject;

    public EditableEntityTableModel(List<Column<E, ?>> columns, DataAccessObject<E> dataAccessObject) {
        super(columns);
        this.rows = new ArrayList<>();
        this.dataAccessObject = dataAccessObject;

        SwingWorker<ArrayList<E>, Void> swingWorker = new SwingWorker<>() {
            @Override
            protected ArrayList<E> doInBackground() {
                return new ArrayList<>(dataAccessObject.findAll());
            }

            @Override
            protected void done() {
                try {
                    setRows(get());
                } catch (Exception e) {
                    ErrorDialog.show(I18N.getFormattedMessage("loadError"));
                }
            }
        };
        swingWorker.execute();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public void deleteRows(List<Integer> rowIndexes) {
        var sorted = rowIndexes
                .stream()
                .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        SwingWorker<Void, Void> swingWorker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {
                for (var rowIndex:
                     sorted) {
                    dataAccessObject.delete(rows.get(rowIndex));
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    updateRows(sorted);
                } catch (Exception ex) {
                    ErrorDialog.show(I18N.getFormattedMessage("UnexpectedDeleteError"));
                }
            }
        };
        swingWorker.execute();
    }

    @Override
    public void updateRows(List<Integer> indexes) {
        for (var rowIndex:
             indexes) {
            rows.remove((int) rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    @Override
    public void addRow(E entity) {
        int newRowIndex = rows.size();
        SwingWorker<Void, Void> swingWorker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {
                dataAccessObject.create(entity);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    rows.add(entity);
                    fireTableRowsInserted(newRowIndex, newRowIndex);
                } catch (Exception ex) {
                    ErrorDialog.show(I18N.getFormattedMessage("addRowError", entity.toString()));
                }
            }
        };
        swingWorker.execute();
    }

    private void setRows(List<E> rows) {
        this.rows.clear();
        this.rows.addAll(rows);
        fireTableDataChanged();
    }

    @Override
    public void updateRow(E entity) {
        SwingWorker<Void, Void> swingWorker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {
                dataAccessObject.update(entity);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    int rowIndex = rows.indexOf(entity);
                    fireTableRowsUpdated(rowIndex, rowIndex);
                }
                catch (Exception ex) {
                    ErrorDialog.show(I18N.getFormattedMessage("updateRowError", entity.toString()));
                }
            }
        };
        swingWorker.execute();
    }

    @Override
    public E getEntity(int rowIndex) {
        return rows.get(rowIndex);
    }
}
