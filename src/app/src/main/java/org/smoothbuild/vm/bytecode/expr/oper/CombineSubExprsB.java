package org.smoothbuild.vm.bytecode.expr.oper;

import org.smoothbuild.vm.bytecode.expr.ExprB;

import com.google.common.collect.ImmutableList;

public record CombineSubExprsB(ImmutableList<ExprB> items) implements SubExprsB {
  @Override
  public ImmutableList<ExprB> toList() {
    return items;
  }
}
