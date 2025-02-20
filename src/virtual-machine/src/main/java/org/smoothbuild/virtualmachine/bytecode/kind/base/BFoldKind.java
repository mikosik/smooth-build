package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold;

public final class BFoldKind extends BOperationKind {
  public BFoldKind(Hash hash, BType evaluationType) {
    super(hash, "FOLD", BFold.class, evaluationType);
  }

  @Override
  public BType evaluationType() {
    return super.evaluationType();
  }

  @Override
  public BFold newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BFoldKind);
    return new BFold(merkleRoot, exprDb);
  }
}
