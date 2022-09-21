package org.smoothbuild.bytecode.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.DEF_FUNC;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.DefFuncB;
import org.smoothbuild.bytecode.hashed.Hash;

public final class DefFuncCB extends FuncCB {
  public DefFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, DEF_FUNC, funcTB);
  }

  @Override
  public DefFuncB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof DefFuncCB);
    return new DefFuncB(merkleRoot, bytecodeDb);
  }
}
