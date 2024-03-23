package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BReference;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class BReferenceCategory extends BOperCategory {
  public BReferenceCategory(Hash hash, BType evaluationType) {
    super(hash, "REFERENCE", BReference.class, evaluationType);
  }

  @Override
  public BReference newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BReferenceCategory);
    return new BReference(merkleRoot, exprDb);
  }
}
