package org.smoothbuild.vm.task;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.InstB;

public record Output(InstB instB, ArrayB messages) {
  public Output {
    requireNonNull(messages);
  }
}
