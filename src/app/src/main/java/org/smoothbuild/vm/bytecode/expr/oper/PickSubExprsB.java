package org.smoothbuild.vm.bytecode.expr.oper;

import io.vavr.collection.Array;
import org.smoothbuild.vm.bytecode.expr.ExprB;

public record PickSubExprsB(ExprB pickable, ExprB index) implements SubExprsB {
  @Override
  public Array<ExprB> toList() {
    return Array.of(pickable, index);
  }
}
