package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;

public class BReferenceKind extends BOperationKind {
  public BReferenceKind(Hash hash, BType evaluationType) {
    super(hash, "REFERENCE", BReference.class, evaluationType);
  }

  @Override
  public BReference newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BReferenceKind);
    return new BReference(merkleRoot, exprDb);
  }
}
