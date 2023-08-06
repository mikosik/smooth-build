package org.smoothbuild.virtualmachine.evaluate.task;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;

public record Output(ValueB valueB, ArrayB messages) {
  public Output {
    requireNonNull(messages);
  }
}
