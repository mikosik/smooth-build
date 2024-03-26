package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;

/**
 * This class is immutable.
 */
public class BSelectKind extends BOperationKind {
  public BSelectKind(Hash hash, BType evaluationType) {
    super(hash, "SELECT", BSelect.class, evaluationType);
  }

  @Override
  public BSelect newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BSelectKind);
    return new BSelect(merkleRoot, exprDb);
  }
}
