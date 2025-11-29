package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;

/**
 * This class is immutable.
 */
public final class BCombineKind extends BOperationKind {
  public BCombineKind(Hash hash, BTupleType evaluationType) {
    super(hash, "COMBINE", BCombine.class, evaluationType);
  }

  @Override
  public BTupleType evaluationType() {
    return (BTupleType) super.evaluationType();
  }

  @Override
  public BCombine newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BCombineKind);
    return new BCombine(merkleRoot, exprDb);
  }
}
