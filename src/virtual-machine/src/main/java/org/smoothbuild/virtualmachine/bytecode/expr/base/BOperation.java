package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BOperationKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * Operation.
 * This class is thread-safe.
 */
public abstract sealed class BOperation extends BExpr
    permits BCall, BCombine, BIf, BInvoke, BMap, BOrder, BPick, BReference, BSelect {
  public BOperation(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BOperationKind);
  }

  @Override
  public BOperationKind kind() {
    return (BOperationKind) super.kind();
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
