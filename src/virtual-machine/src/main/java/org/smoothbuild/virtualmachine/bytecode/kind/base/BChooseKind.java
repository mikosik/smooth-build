package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose;

/**
 * This class is immutable.
 */
public final class BChooseKind extends BOperationKind {
  public BChooseKind(Hash hash, BType evaluationType) {
    super(hash, "CHOOSE", BChoose.class, evaluationType);
    checkArgument(evaluationType instanceof BChoiceType);
  }

  @Override
  public BChoiceType evaluationType() {
    return (BChoiceType) super.evaluationType();
  }

  @Override
  public BChoose newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BChooseKind);
    return new BChoose(merkleRoot, exprDb);
  }
}
