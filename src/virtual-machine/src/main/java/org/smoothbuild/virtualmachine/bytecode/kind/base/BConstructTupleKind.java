package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructTuple;

/**
 * This class is immutable.
 */
public final class BConstructTupleKind extends BOperationKind {
  public BConstructTupleKind(Hash hash, BTupleType evaluationType) {
    super(hash, "CONSTRUCT_TUPLE", BConstructTuple.class, evaluationType);
  }

  @Override
  public BTupleType evaluationType() {
    return (BTupleType) super.evaluationType();
  }

  @Override
  public BConstructTuple newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BConstructTupleKind);
    return new BConstructTuple(merkleRoot, exprDb);
  }
}
