package org.smoothbuild.vm.bytecode.expr.oper;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;

import com.google.common.collect.ImmutableList;

public record SelectSubExprsB(ExprB selectable, IntB index) implements SubExprsB {
  @Override
  public ImmutableList<ExprB> toList() {
    return list(selectable, index);
  }
}
