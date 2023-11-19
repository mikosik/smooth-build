package org.smoothbuild.vm.bytecode.expr.oper;

import io.vavr.collection.Array;
import org.smoothbuild.vm.bytecode.expr.ExprB;

public record CombineSubExprsB(Array<ExprB> items) implements SubExprsB {
  @Override
  public Array<ExprB> toList() {
    return items;
  }
}
