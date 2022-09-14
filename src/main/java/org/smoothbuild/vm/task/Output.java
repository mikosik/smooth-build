package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.ValB;

public record Output(ValB valB, ArrayB messages) {

  public Output {
    requireNonNull(messages);
  }

  public boolean hasVal() {
    return valB != null;
  }

  public ValB valB() {
    checkState(hasVal(), "Output does not contain value.");
    return valB;
  }
}
