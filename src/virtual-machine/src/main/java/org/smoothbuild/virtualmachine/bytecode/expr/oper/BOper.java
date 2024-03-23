package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BOperCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * Operation.
 * This class is thread-safe.
 */
public abstract class BOper extends BExpr {
  public BOper(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof BOperCategory);
  }

  @Override
  public BOperCategory category() {
    return (BOperCategory) super.category();
  }

  @Override
  public BType evaluationType() {
    return category().evaluationType();
  }

  public abstract BExprs subExprs() throws BytecodeException;

  @Override
  public String exprToString() throws BytecodeException {
    return category().name() + ":" + evaluationType() + "(???)";
  }
}
