package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BNativeFunc;

public final class BNativeFuncKind extends BFuncKind {
  public BNativeFuncKind(Hash hash, BFuncType funcType) {
    super(hash, "NATIVE_FUNC", funcType, BNativeFunc.class);
  }

  @Override
  public BNativeFunc newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BNativeFuncKind);
    return new BNativeFunc(merkleRoot, exprDb);
  }
}
