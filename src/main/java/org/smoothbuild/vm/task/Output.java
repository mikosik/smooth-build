package org.smoothbuild.vm.task;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.bytecode.expr.value.ArrayB;
import org.smoothbuild.bytecode.expr.value.ValueB;

public record Output(ValueB valueB, ArrayB messages) {
  public Output {
    requireNonNull(messages);
  }
}
