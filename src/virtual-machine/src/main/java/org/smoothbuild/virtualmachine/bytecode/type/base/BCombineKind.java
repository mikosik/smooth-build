package org.smoothbuild.virtualmachine.bytecode.type.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;

/**
 * This class is immutable.
 */
public class BCombineKind extends BOperKind {
  public BCombineKind(Hash hash, BType evaluationType) {
    super(hash, "COMBINE", BCombine.class, evaluationType);
    checkArgument(evaluationType instanceof BTupleType);
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
