package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambdaRef;

/**
 * This class is immutable.
 */
public final class BLambdaRefKind extends BOperationKind {
  public BLambdaRefKind(Hash hash, BLambdaType evaluationType) {
    super(hash, "LAMBDA_REF", BLambdaRef.class, evaluationType);
  }

  @Override
  public BLambdaType evaluationType() {
    return (BLambdaType) super.evaluationType();
  }

  @Override
  public BLambdaRef newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BLambdaRefKind);
    return new BLambdaRef(merkleRoot, exprDb);
  }
}
