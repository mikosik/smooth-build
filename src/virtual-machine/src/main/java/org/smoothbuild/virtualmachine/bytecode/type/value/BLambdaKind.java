package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BLambda;

public final class BLambdaKind extends BFuncKind {
  public BLambdaKind(Hash hash, BFuncType funcType) {
    super(hash, "LAMBDA", funcType, BLambda.class);
  }

  @Override
  public BLambda newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BLambdaKind);
    return new BLambda(merkleRoot, exprDb);
  }
}
