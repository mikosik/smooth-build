package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructVariant;

/**
 * This class is immutable.
 */
public final class BConstructVariantKind extends BOperationKind {
  public BConstructVariantKind(Hash hash, BVariantType evaluationType) {
    super(hash, "CONSTRUCT_VARIANT", BConstructVariant.class, evaluationType);
  }

  @Override
  public BVariantType evaluationType() {
    return (BVariantType) super.evaluationType();
  }

  @Override
  public BConstructVariant newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BConstructVariantKind);
    return new BConstructVariant(merkleRoot, exprDb);
  }
}
