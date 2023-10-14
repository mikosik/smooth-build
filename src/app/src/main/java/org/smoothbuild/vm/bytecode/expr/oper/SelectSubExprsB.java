package org.smoothbuild.vm.bytecode.expr.oper;

import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;

import io.vavr.collection.Array;

public record SelectSubExprsB(ExprB selectable, IntB index) implements SubExprsB {
  @Override
  public Array<ExprB> toList() {
    return Array.of(selectable, index);
  }
}
