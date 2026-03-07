package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet;

/**
 * This class is immutable.
 */
public final class BArrayGetKind extends BOperationKind {
  public BArrayGetKind(Hash hash, BType evaluationType) {
    super(hash, "ARRAY_GET", BArrayGet.class, evaluationType);
  }

  @Override
  public BArrayGet newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BArrayGetKind);
    return new BArrayGet(merkleRoot, exprDb);
  }
}
