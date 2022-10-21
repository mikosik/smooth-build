package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.InstB;

public record Output(InstB instB, ArrayB messages) {
  public Output {
    requireNonNull(messages);
  }

  public boolean hasVal() {
    return instB != null;
  }

  @Override
  public InstB instB() {
    checkState(hasVal(), "Output does not contain value.");
    return instB;
  }
}
