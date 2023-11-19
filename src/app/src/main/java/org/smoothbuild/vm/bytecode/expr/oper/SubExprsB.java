package org.smoothbuild.vm.bytecode.expr.oper;

import io.vavr.collection.Array;
import org.smoothbuild.vm.bytecode.expr.ExprB;

public interface SubExprsB {
  public Array<ExprB> toList();
}
