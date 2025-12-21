package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
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
        BLambdaRef,
        BParamRef,
        BSelect,
        BSwitch {
  private final String name;
  private final int subExprsCount;

  public BOperation(MerkleRoot merkleRoot, BExprDb exprDb, int subExprsCount) {
    checkArgument(merkleRoot.kind() instanceof BOperationKind);
    super(merkleRoot, exprDb);
    this.subExprsCount = subExprsCount;
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

  public List<BExpr> subExprs() throws BytecodeException {
    return readDataAsExprChain(subExprsCount);
  }
}
