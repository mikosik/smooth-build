package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;

/**
 * This class is immutable.
 */
public final class BSwitchKind extends BOperationKind {
  public BSwitchKind(Hash hash, BType evaluationType) {
    super(hash, "SWITCH", BSwitch.class, evaluationType);
  }

  @Override
  public BSwitch newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BSwitchKind);
    return new BSwitch(merkleRoot, exprDb);
  }
}
