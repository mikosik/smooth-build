package org.smoothbuild.vm.bytecode.expr.oper;

import io.vavr.collection.Array;
import org.smoothbuild.vm.bytecode.expr.ExprB;

public record OrderSubExprsB(Array<ExprB> elements) implements SubExprsB {
  @Override
  public Array<ExprB> toList() {
    return elements;
  }
}
