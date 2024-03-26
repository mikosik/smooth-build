package org.smoothbuild.virtualmachine.bytecode.type.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;

public final class BMapKind extends BFuncKind {
  public BMapKind(Hash hash, BFuncType funcType) {
    super(hash, "MAP", funcType, BMap.class);
  }

  @Override
  public BMap newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BMapKind);
    return new BMap(merkleRoot, exprDb);
  }

  @Override
  public boolean containsData() {
    return false;
  }
}
