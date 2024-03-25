package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BOperKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * Operation.
 * This class is thread-safe.
 */
public abstract sealed class BOper extends BExpr
    permits BCall, BCombine, BIf, BOrder, BPick, BReference, BSelect {
  public BOper(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BOperKind);
  }

  @Override
  public BOperKind kind() {
    return (BOperKind) super.kind();
  }

  @Override
  public BType evaluationType() {
    return kind().evaluationType();
  }

  public abstract BExprs subExprs() throws BytecodeException;

  @Override
  public String exprToString() throws BytecodeException {
    return kind().name() + ":" + evaluationType() + "(???)";
  }
}
