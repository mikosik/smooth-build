package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;

public final class BLambdaType extends BType {
  private final BTupleType params;
  private final BType result;

  public BLambdaType(Hash hash, BTupleType params, BType result) {
    super(hash, BTypeNames.lambdaTypeName(params.elements(), result), BLambda.class);
    this.params = params;
    this.result = result;
  }

  public BTupleType params() {
    return params;
  }

  public BType result() {
    return result;
  }

  @Override
  public BLambda newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BLambdaType);
    return new BLambda(merkleRoot, exprDb);
  }
}
