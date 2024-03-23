package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * This class is immutable.
 */
public class BSelectCategory extends BOperCategory {
  public BSelectCategory(Hash hash, BType evaluationType) {
    super(hash, "SELECT", BSelect.class, evaluationType);
  }

  @Override
  public BSelect newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BSelectCategory);
    return new BSelect(merkleRoot, exprDb);
  }
}
