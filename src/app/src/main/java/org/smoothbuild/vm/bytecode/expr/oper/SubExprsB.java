package org.smoothbuild.vm.bytecode.expr.oper;

import org.smoothbuild.vm.bytecode.expr.ExprB;

import com.google.common.collect.ImmutableList;

public interface SubExprsB {
  public ImmutableList<ExprB> toList();
}
