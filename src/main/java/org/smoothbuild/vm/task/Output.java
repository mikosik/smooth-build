package org.smoothbuild.vm.task;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.ValueB;

public record Output(ValueB valueB, ArrayB messages) {
  public Output {
    requireNonNull(messages);
  }
}
