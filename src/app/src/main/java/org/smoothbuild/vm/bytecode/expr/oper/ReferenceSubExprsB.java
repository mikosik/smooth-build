package org.smoothbuild.vm.bytecode.expr.oper;

import org.smoothbuild.vm.bytecode.expr.ExprB;

import io.vavr.collection.Array;

public record ReferenceSubExprsB() implements SubExprsB {
  @Override
  public Array<ExprB> toList() {
    return Array.empty();
  }
}
