package org.smoothbuild.vm.bytecode.expr.oper;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;

public record SelectSubExprsB(ExprB selectable, IntB index) implements SubExprsB {
  @Override
  public List<ExprB> toList() {
    return list(selectable, index);
  }
}
