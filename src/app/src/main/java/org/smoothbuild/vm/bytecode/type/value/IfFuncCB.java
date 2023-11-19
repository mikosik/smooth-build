package org.smoothbuild.vm.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.IF_FUNC;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.IfFuncB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public final class IfFuncCB extends FuncCB {
  public IfFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, IF_FUNC, funcTB);
  }

  @Override
  public IfFuncB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof IfFuncCB);
    return new IfFuncB(merkleRoot, bytecodeDb);
  }

  @Override
  public boolean containsData() {
    return false;
  }
}
