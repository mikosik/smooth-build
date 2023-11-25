package org.smoothbuild.vm.bytecode.expr.oper;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.expr.ExprB;

public record CallSubExprsB(ExprB func, CombineB args) implements SubExprsB {
  @Override
  public List<ExprB> toList() {
    return list(func, args());
  }
}
