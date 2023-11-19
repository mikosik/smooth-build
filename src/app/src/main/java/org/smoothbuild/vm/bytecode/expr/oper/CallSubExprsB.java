package org.smoothbuild.vm.bytecode.expr.oper;

import io.vavr.collection.Array;
import org.smoothbuild.vm.bytecode.expr.ExprB;

public record CallSubExprsB(ExprB func, CombineB args) implements SubExprsB {
  @Override
  public Array<ExprB> toList() {
    return Array.of(func, args());
  }
}
