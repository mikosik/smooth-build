package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.REFERENCE;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.ReferenceB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class ReferenceCB extends OperCB {
  public ReferenceCB(Hash hash, TypeB evaluationType) {
    super(hash, REFERENCE, evaluationType);
  }

  @Override
  public ReferenceB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof ReferenceCB);
    return new ReferenceB(merkleRoot, exprDb);
  }
}
