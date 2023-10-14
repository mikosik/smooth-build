package org.smoothbuild.vm.bytecode.expr.oper;

import org.smoothbuild.vm.bytecode.expr.ExprB;

import io.vavr.collection.Array;

public record CombineSubExprsB(Array<ExprB> items) implements SubExprsB {
  @Override
  public Array<ExprB> toList() {
    return items;
  }
}
