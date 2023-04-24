package org.smoothbuild.vm.bytecode.expr.oper;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.vm.bytecode.expr.ExprB;

import com.google.common.collect.ImmutableList;

public record ReferenceSubExprsB() implements SubExprsB {
  @Override
  public ImmutableList<ExprB> toList() {
    return list();
  }
}
