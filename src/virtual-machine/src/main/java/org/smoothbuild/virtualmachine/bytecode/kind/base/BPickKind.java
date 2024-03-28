package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;

/**
 * This class is immutable.
 */
public final class BPickKind extends BOperationKind {
  public BPickKind(Hash hash, BType evaluationType) {
    super(hash, "PICK", BPick.class, evaluationType);
  }

  @Override
  public BPick newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BPickKind);
    return new BPick(merkleRoot, exprDb);
  }
}
