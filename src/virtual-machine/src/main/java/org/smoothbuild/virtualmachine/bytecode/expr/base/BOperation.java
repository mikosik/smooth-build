package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
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
    permits BCall,
        BChoose,
        BCombine,
        BFold,
        BIf,
        BInvoke,
        BMap,
        BOrder,
        BPick,
        BReference,
        BSelect,
        BSwitch {
  private final String name;

  public BOperation(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BOperationKind);
    super(merkleRoot, exprDb);
    this.name = UPPER_CAMEL.to(LOWER_CAMEL, getClass().getSimpleName().substring(1));
  }

  public String name() {
    return name;
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
}
