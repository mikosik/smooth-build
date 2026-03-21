package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructArray;

/**
 * This class is immutable.
 */
public final class BConstructArrayKind extends BOperationKind {
  public BConstructArrayKind(Hash hash, BArrayType evaluationType) {
    super(hash, "CONSTRUCT_ARRAY", BConstructArray.class, evaluationType);
  }

  @Override
  public BArrayType evaluationType() {
    return (BArrayType) super.evaluationType();
  }

  @Override
  public BConstructArray newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BConstructArrayKind);
    return new BConstructArray(merkleRoot, exprDb);
  }
}
