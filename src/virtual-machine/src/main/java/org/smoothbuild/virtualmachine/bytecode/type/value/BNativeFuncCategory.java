package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BNativeFunc;

public final class BNativeFuncCategory extends BFuncCategory {
  public BNativeFuncCategory(Hash hash, BFuncType funcType) {
    super(hash, "NATIVE_FUNC", funcType, BNativeFunc.class);
  }

  @Override
  public BNativeFunc newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BNativeFuncCategory);
    return new BNativeFunc(merkleRoot, exprDb);
  }
}
