package org.smoothbuild.virtualmachine.bytecode.kind.base;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;

public abstract class BOperKind extends BKind {
  private final BType evaluationType;

  protected BOperKind(
      Hash hash, String name, Class<? extends BExpr> javaType, BType evaluationType) {
    super(hash, name, javaType);
    this.evaluationType = evaluationType;
  }

  public BType evaluationType() {
    return evaluationType;
  }
}
