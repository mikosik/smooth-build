package org.smoothbuild.bytecode.expr.inst;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.type.inst.FuncCB;
import org.smoothbuild.bytecode.type.inst.FuncTB;

public abstract sealed class FuncB extends ValueB
    permits DefFuncB, IfFuncB, MapFuncB, NatFuncB {
  public FuncB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof FuncCB);
  }

  @Override
  public FuncTB evalT() {
    return type();
  }

  @Override
  public FuncTB type() {
    return ((FuncCB) category()).type();
  }
}
