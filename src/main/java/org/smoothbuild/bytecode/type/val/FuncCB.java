package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.bytecode.type.CatKindB;

public sealed abstract class FuncCB extends CatB
    permits DefFuncCB, IfFuncCB, MapFuncCB, NatFuncCB {
  private final FuncTB funcTB;

  public FuncCB(Hash hash, CatKindB kind, FuncTB funcTB) {
    super(hash, kind.name() + ":" + funcTB, kind);
    this.funcTB = funcTB;
  }

  public FuncTB type() {
    return funcTB;
  }
}
