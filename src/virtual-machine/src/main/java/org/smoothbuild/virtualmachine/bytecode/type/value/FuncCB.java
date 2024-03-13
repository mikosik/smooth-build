package org.smoothbuild.virtualmachine.bytecode.type.value;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;

public abstract sealed class FuncCB extends CategoryB
    permits LambdaCB, IfFuncCB, MapFuncCB, NativeFuncCB {
  private final FuncTB funcTB;

  public FuncCB(Hash hash, String name, FuncTB funcTB, Class<? extends ExprB> javaType) {
    super(hash, name, javaType);
    this.funcTB = funcTB;
  }

  public FuncTB type() {
    return funcTB;
  }
}
