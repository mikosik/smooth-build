package org.smoothbuild.virtualmachine.bytecode.type.value;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.type.BCategory;

public abstract sealed class BFuncCategory extends BCategory
    permits BLambdaCategory, BIfCategory, BMapCategory, BNativeFuncCategory {
  private final BFuncType funcType;

  public BFuncCategory(
      Hash hash, String name, BFuncType funcType, Class<? extends BExpr> javaType) {
    super(hash, name, javaType);
    this.funcType = funcType;
  }

  public BFuncType type() {
    return funcType;
  }
}
