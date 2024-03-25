package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class BReferenceKind extends BOperKind {
  public BReferenceKind(Hash hash, BType evaluationType) {
    super(hash, "REFERENCE", BReference.class, evaluationType);
  }

  @Override
  public BReference newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BReferenceKind);
    return new BReference(merkleRoot, exprDb);
  }
}
