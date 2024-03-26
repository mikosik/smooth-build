package org.smoothbuild.virtualmachine.bytecode.type.base;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;

public abstract class BType extends BKind {
  protected BType(Hash hash, String name, Class<? extends BExpr> javaType) {
    super(hash, name, javaType);
  }
}
