package org.smoothbuild.vm.bytecode.expr.oper;

import io.vavr.collection.Array;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;

public record SelectSubExprsB(ExprB selectable, IntB index) implements SubExprsB {
  @Override
  public Array<ExprB> toList() {
    return Array.of(selectable, index);
  }
}
