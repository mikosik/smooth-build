package org.smoothbuild.virtualmachine.bytecode.type.oper;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.type.BCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public abstract class BOperCategory extends BCategory {
  private final BType evaluationType;

  protected BOperCategory(
      Hash hash, String name, Class<? extends BExpr> javaType, BType evaluationType) {
    super(hash, name, javaType);
    this.evaluationType = evaluationType;
  }

  public BType evaluationType() {
    return evaluationType;
  }
}
