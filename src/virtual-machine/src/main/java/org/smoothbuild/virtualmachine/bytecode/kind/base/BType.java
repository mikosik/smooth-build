package org.smoothbuild.virtualmachine.bytecode.kind.base;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;

public abstract sealed class BType extends BKind
    permits BArrayType,
        BBlobType,
        BBoolType,
        BChoiceType,
        BIntType,
        BLambdaType,
        BStringType,
        BTupleType {
  protected BType(Hash hash, String name, Class<? extends BExpr> javaType) {
    super(hash, name, javaType);
  }
}
