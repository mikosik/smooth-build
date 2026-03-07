package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateArray;

/**
 * This class is immutable.
 */
public final class BCreateArrayKind extends BOperationKind {
  public BCreateArrayKind(Hash hash, BArrayType evaluationType) {
    super(hash, "CREATE_ARRAY", BCreateArray.class, evaluationType);
  }

  @Override
  public BArrayType evaluationType() {
    return (BArrayType) super.evaluationType();
  }

  @Override
  public BCreateArray newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BCreateArrayKind);
    return new BCreateArray(merkleRoot, exprDb);
  }
}
