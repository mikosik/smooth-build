package org.smoothbuild.bytecode.expr.val;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.type.val.FuncCB;
import org.smoothbuild.bytecode.type.val.FuncTB;

public abstract sealed class FuncB extends InstB
    permits IfFuncB, MapFuncB, DefFuncB, NatFuncB {
  public FuncB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof FuncCB);
  }

  @Override
  public FuncTB type() {
    return ((FuncCB) category()).type();
  }
}
