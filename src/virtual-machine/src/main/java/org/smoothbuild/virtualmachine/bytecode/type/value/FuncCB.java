package org.smoothbuild.virtualmachine.bytecode.type.value;

import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB;

public abstract sealed class FuncCB extends CategoryB
    permits LambdaCB, IfFuncCB, MapFuncCB, NativeFuncCB {
  private final FuncTB funcTB;

  public FuncCB(Hash hash, CategoryKindB kind, FuncTB funcTB) {
    super(hash, kind.name() + ":" + funcTB, kind);
    this.funcTB = funcTB;
  }

  public FuncTB type() {
    return funcTB;
  }
}
