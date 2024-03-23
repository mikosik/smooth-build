package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * This class is immutable.
 */
public class BOrderCategory extends BOperCategory {
  public BOrderCategory(Hash hash, BType evaluationType) {
    super(hash, "ORDER", BOrder.class, evaluationType);
    checkArgument(evaluationType instanceof BArrayType);
  }

  @Override
  public BArrayType evaluationType() {
    return (BArrayType) super.evaluationType();
  }

  @Override
  public BOrder newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BOrderCategory);
    return new BOrder(merkleRoot, exprDb);
  }
}
