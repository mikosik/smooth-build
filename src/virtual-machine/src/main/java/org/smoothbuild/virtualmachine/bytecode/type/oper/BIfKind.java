package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public final class BIfKind extends BOperKind {
  public BIfKind(Hash hash, BType evaluationType) {
    super(hash, "IF", BIf.class, evaluationType);
  }

  @Override
  public BIf newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BIfKind);
    return new BIf(merkleRoot, exprDb);
  }
}
