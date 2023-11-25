package org.smoothbuild.vm.bytecode.expr.oper;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.expr.ExprB;

public record OrderSubExprsB(List<ExprB> elements) implements SubExprsB {
  @Override
  public List<ExprB> toList() {
    return elements;
  }
}
