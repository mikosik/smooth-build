package org.smoothbuild.virtualmachine.bytecode.type.value;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.type.BKind;

public abstract class BType extends BKind {
  protected BType(Hash hash, String name, Class<? extends BExpr> javaType) {
    super(hash, name, javaType);
  }
}
