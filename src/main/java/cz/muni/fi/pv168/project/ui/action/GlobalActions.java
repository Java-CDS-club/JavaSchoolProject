package cz.muni.fi.pv168.project.ui.action;

import javax.swing.*;
import java.util.Objects;

public final class GlobalActions {

    private final Action addAction;
    private final Action deleteAction;
    private final Action editAction;

    private GlobalActions(Action addAction, Action deleteAction, Action editAction) {
        this.addAction = Objects.requireNonNull(addAction);
        this.deleteAction = Objects.requireNonNull(deleteAction);
        this.editAction = Objects.requireNonNull(editAction);
    }

    public static EntityActionsBuilder builder() {
        return new EntityActionsBuilder();
    }

    public Action getAddAction() {
        return this.addAction;
    }

    public Action getDeleteAction() {
        return this.deleteAction;
    }

    public Action getEditAction() {
        return this.editAction;
    }

    public static class EntityActionsBuilder {

        private Action addAction;
        private Action deleteAction;
        private Action editAction;

        private EntityActionsBuilder() {
        }

        public EntityActionsBuilder addAction(Action addAction) {
            this.addAction = addAction;
            return this;
        }

        public EntityActionsBuilder deleteAction(Action deleteAction) {
            this.deleteAction = deleteAction;
            return this;
        }

        public EntityActionsBuilder editAction(Action editAction) {
            this.editAction = editAction;
            return this;
        }

        public GlobalActions build() {
            return new GlobalActions(addAction, deleteAction, editAction);
        }
    }
}
