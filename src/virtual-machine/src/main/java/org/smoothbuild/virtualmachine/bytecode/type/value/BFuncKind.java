package org.smoothbuild.virtualmachine.bytecode.type.value;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.type.BKind;

public abstract sealed class BFuncKind extends BKind
    permits BLambdaKind, BMapKind, BNativeFuncKind {
  private final BFuncType funcType;

  public BFuncKind(Hash hash, String name, BFuncType funcType, Class<? extends BExpr> javaType) {
    super(hash, name, javaType);
    this.funcType = funcType;
  }

  public BFuncType type() {
    return funcType;
  }
}
