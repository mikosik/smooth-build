package org.smoothbuild.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.NATIVE_FUNC;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.bytecode.hashed.Hash;

public final class NativeFuncCB extends FuncCB {
  public NativeFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, NATIVE_FUNC, funcTB);
  }

  @Override
  public NativeFuncB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof NativeFuncCB);
    return new NativeFuncB(merkleRoot, bytecodeDb);
  }
}
