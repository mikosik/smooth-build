package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;

public class BMapKind extends BOperationKind {
  public BMapKind(Hash hash, BType evaluationType) {
    super(hash, "MAP", BMap.class, evaluationType);
    checkArgument(evaluationType instanceof BArrayType);
  }

  @Override
  public BArrayType evaluationType() {
    return (BArrayType) super.evaluationType();
  }

  @Override
  public BMap newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BMapKind);
    return new BMap(merkleRoot, exprDb);
  }
}
