package org.smoothbuild.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.DEFINED_FUNC;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.value.DefinedFuncB;
import org.smoothbuild.bytecode.hashed.Hash;

public final class DefinedFuncCB extends FuncCB {
  public DefinedFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, DEFINED_FUNC, funcTB);
  }

  @Override
  public DefinedFuncB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof DefinedFuncCB);
    return new DefinedFuncB(merkleRoot, bytecodeDb);
  }
}
