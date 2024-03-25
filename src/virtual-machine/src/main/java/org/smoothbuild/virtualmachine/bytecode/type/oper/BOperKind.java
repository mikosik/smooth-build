package org.smoothbuild.virtualmachine.bytecode.type.oper;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.type.BKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

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
