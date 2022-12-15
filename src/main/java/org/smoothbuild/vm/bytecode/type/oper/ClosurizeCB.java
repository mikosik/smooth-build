package org.smoothbuild.vm.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.CLOSURIZE;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.oper.ClosurizeB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;

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
