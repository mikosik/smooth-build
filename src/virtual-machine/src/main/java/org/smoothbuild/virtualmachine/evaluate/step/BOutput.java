package org.smoothbuild.virtualmachine.evaluate.step;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public record BOutput(BValue value, BArray storedLogs) {
  public BOutput {
    requireNonNull(storedLogs);
  }
}
