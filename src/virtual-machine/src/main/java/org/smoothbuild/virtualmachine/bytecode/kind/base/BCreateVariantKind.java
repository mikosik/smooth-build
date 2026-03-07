package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateVariant;

/**
 * This class is immutable.
 */
public final class BCreateVariantKind extends BOperationKind {
  public BCreateVariantKind(Hash hash, BVariantType evaluationType) {
    super(hash, "CREATE_VARIANT", BCreateVariant.class, evaluationType);
  }

  @Override
  public BVariantType evaluationType() {
    return (BVariantType) super.evaluationType();
  }

  @Override
  public BCreateVariant newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BCreateVariantKind);
    return new BCreateVariant(merkleRoot, exprDb);
  }
}
