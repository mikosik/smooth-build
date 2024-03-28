package org.smoothbuild.virtualmachine.bytecode.kind.base;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;

public abstract sealed class BOperationKind extends BKind
    permits BCallKind,
        BCombineKind,
        BIfKind,
        BInvokeKind,
        BMapKind,
        BOrderKind,
        BPickKind,
        BReferenceKind,
        BSelectKind {
  private final BType evaluationType;

  protected BOperationKind(
      Hash hash, String name, Class<? extends BExpr> javaType, BType evaluationType) {
    super(hash, name, javaType);
    this.evaluationType = evaluationType;
  }

  public BType evaluationType() {
    return evaluationType;
  }
}
