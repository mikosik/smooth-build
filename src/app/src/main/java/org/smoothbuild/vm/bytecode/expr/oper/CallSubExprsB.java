package org.smoothbuild.vm.bytecode.expr.oper;

import static org.smoothbuild.common.collect.Lists.list;

import org.smoothbuild.vm.bytecode.expr.ExprB;

import com.google.common.collect.ImmutableList;

public record CallSubExprsB(ExprB func, CombineB args) implements SubExprsB {
  @Override
  public ImmutableList<ExprB> toList() {
    return list(func, args());
  }
}
