package org.smoothbuild.vm.evaluate.task;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

public record Output(ValueB valueB, ArrayB messages) {
  public Output {
    requireNonNull(messages);
  }
}
