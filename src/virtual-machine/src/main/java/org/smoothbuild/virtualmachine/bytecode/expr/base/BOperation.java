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
 * Operation. This class is thread-safe.
 */
public abstract sealed class BOperation extends BExpr
    permits BArrayGet,
        BCall,
        BConstructArray,
        BConstructTuple,
        BConstructVariant,
        BFold,
        BIf,
        BInvoke,
        BMap,
        BRef,
        BSwitch,
        BTupleGet {
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

  /**
   * Returns the list of sub expressions without validating whether list size matches the expected
   * count of subexpressions for this operation kind nor whether their types matches type expected
   * by this operation kind.
   */
  public List<BExpr> unvalidatedSubExprs() throws BytecodeException {
    return readDataAsExprChain(subExprsCount);
  }
}
