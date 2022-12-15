package org.smoothbuild.vm.bytecode.type.value;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB;

public sealed abstract class FuncCB extends CategoryB
    permits ClosureCB, ExprFuncCB, IfFuncCB, MapFuncCB, NativeFuncCB {
  private final FuncTB funcTB;

  public FuncCB(Hash hash, CategoryKindB kind, FuncTB funcTB) {
    super(hash, kind.name() + ":" + funcTB, kind);
    this.funcTB = funcTB;
  }

  public FuncTB type() {
    return funcTB;
  }
}
