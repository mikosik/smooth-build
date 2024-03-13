package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class PickCB extends OperCB {
  public PickCB(Hash hash, TypeB evaluationType) {
    super(hash, "PICK", PickB.class, evaluationType);
  }

  @Override
  public PickB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof PickCB);
    return new PickB(merkleRoot, exprDb);
  }
}
