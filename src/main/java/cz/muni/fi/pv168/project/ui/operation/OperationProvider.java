package cz.muni.fi.pv168.project.ui.operation;

import java.util.Optional;

public interface OperationProvider {

    <O extends Operation> Optional<O> getOperation(Class<O> operationType);
}
