package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OperCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * Operation.
 * This class is thread-safe.
 */
public abstract class OperB extends ExprB {
  public OperB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof OperCB);
  }

  @Override
  public OperCB category() {
    return (OperCB) super.category();
  }

  @Override
  public TypeB evaluationType() {
    return category().evaluationType();
  }

  public abstract ExprsB subExprs() throws BytecodeException;

  @Override
  public String exprToString() throws BytecodeException {
    return category().name() + "(???)";
  }
}
