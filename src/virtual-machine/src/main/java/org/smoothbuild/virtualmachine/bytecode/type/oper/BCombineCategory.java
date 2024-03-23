package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCombine;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * This class is immutable.
 */
public class BCombineCategory extends BOperCategory {
  public BCombineCategory(Hash hash, BType evaluationType) {
    super(hash, "COMBINE", BCombine.class, evaluationType);
    checkArgument(evaluationType instanceof BTupleType);
  }

  @Override
  public BTupleType evaluationType() {
    return (BTupleType) super.evaluationType();
  }

  @Override
  public BCombine newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BCombineCategory);
    return new BCombine(merkleRoot, exprDb);
  }
}
