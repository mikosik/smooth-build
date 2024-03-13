package org.smoothbuild.virtualmachine.bytecode.type.oper;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public abstract class OperCB extends CategoryB {
  private final TypeB evaluationType;

  protected OperCB(Hash hash, String name, Class<? extends ExprB> javaType, TypeB evaluationType) {
    super(hash, name, javaType);
    this.evaluationType = evaluationType;
  }

  public TypeB evaluationType() {
    return evaluationType;
  }
}
