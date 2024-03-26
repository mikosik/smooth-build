package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;

/**
 * This class is immutable.
 */
public class BOrderKind extends BOperationKind {
  public BOrderKind(Hash hash, BType evaluationType) {
    super(hash, "ORDER", BOrder.class, evaluationType);
    checkArgument(evaluationType instanceof BArrayType);
  }

  @Override
  public BArrayType evaluationType() {
    return (BArrayType) super.evaluationType();
  }

  @Override
  public BOrder newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BOrderKind);
    return new BOrder(merkleRoot, exprDb);
  }
}
