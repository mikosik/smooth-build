package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;

public final class BInvokeKind extends BOperationKind {
  public BInvokeKind(Hash hash, BType evaluationType) {
    super(hash, "INVOKE", BInvoke.class, evaluationType);
  }

  @Override
  public BInvoke newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BInvokeKind);
    return new BInvoke(merkleRoot, exprDb);
  }
}
