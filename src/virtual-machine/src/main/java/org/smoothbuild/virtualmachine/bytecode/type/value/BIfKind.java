package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BIf;

public final class BIfKind extends BFuncKind {
  public BIfKind(Hash hash, BFuncType funcType) {
    super(hash, "IF", funcType, BIf.class);
  }

  @Override
  public BIf newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BIfKind);
    return new BIf(merkleRoot, exprDb);
  }

  @Override
  public boolean containsData() {
    return false;
  }
}
