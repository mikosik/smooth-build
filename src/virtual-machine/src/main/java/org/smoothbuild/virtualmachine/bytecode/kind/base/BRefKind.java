package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BRef;

public final class BRefKind extends BOperationKind {
  public BRefKind(Hash hash, BType evaluationType) {
    super(hash, "REF", BRef.class, evaluationType);
  }

  @Override
  public BRef newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BRefKind);
    return new BRef(merkleRoot, exprDb);
  }
}
