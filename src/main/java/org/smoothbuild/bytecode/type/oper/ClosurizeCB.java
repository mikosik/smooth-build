package org.smoothbuild.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.CLOSURIZE;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.ClosurizeB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.FuncTB;

public final class ClosurizeCB extends OperCB {
  public ClosurizeCB(Hash hash, FuncTB funcTB) {
    super(hash, CLOSURIZE, funcTB);
  }

  @Override
  public FuncTB evalT() {
    return ((FuncTB) super.evalT());
  }

  @Override
  public ClosurizeB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof ClosurizeCB);
    return new ClosurizeB(merkleRoot, bytecodeDb);
  }
}
