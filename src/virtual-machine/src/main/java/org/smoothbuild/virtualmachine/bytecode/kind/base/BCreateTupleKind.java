package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateTuple;

/**
 * This class is immutable.
 */
public final class BCreateTupleKind extends BOperationKind {
  public BCreateTupleKind(Hash hash, BTupleType evaluationType) {
    super(hash, "CREATE_TUPLE", BCreateTuple.class, evaluationType);
  }

  @Override
  public BTupleType evaluationType() {
    return (BTupleType) super.evaluationType();
  }

  @Override
  public BCreateTuple newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BCreateTupleKind);
    return new BCreateTuple(merkleRoot, exprDb);
  }
}
