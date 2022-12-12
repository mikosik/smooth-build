package org.smoothbuild.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.EXPR_FUNC;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.value.ExprFuncB;
import org.smoothbuild.bytecode.hashed.Hash;

public final class ExprFuncCB extends FuncCB {
  public ExprFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, EXPR_FUNC, funcTB);
  }

  @Override
  public ExprFuncB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof ExprFuncCB);
    return new ExprFuncB(merkleRoot, bytecodeDb);
  }
}
