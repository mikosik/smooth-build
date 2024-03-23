package org.smoothbuild.virtualmachine.evaluate.task;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;

public record Output(BValue value, BArray storedLogs) {
  public Output {
    requireNonNull(storedLogs);
  }
}
