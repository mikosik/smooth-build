package org.smoothbuild.vm.bytecode.expr.oper;

import io.vavr.collection.Array;
import org.smoothbuild.vm.bytecode.expr.ExprB;

public record ReferenceSubExprsB() implements SubExprsB {
  @Override
  public Array<ExprB> toList() {
    return Array.empty();
  }
}
