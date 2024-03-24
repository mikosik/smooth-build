package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * This class is immutable.
 */
public class BOrderKind extends BOperKind {
  public BOrderKind(Hash hash, BType evaluationType) {
    super(hash, "ORDER", BOrder.class, evaluationType);
    checkArgument(evaluationType instanceof BArrayType);
  }

  @Override
  public BArrayType evaluationType() {
    return (BArrayType) super.evaluationType();
  }

  @Override
  public BOrder newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BOrderKind);
    return new BOrder(merkleRoot, exprDb);
  }
}
