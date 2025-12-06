package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BParamRef;

public final class BParamRefKind extends BOperationKind {
  public BParamRefKind(Hash hash, BType evaluationType) {
    super(hash, "PARAM_REF", BParamRef.class, evaluationType);
  }

  @Override
  public BParamRef newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BParamRefKind);
    return new BParamRef(merkleRoot, exprDb);
  }
}
