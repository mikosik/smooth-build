package org.smoothbuild.virtualmachine.bytecode.type.value;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.type.BCategory;

public abstract class BType extends BCategory {
  protected BType(Hash hash, String name, Class<? extends BExpr> javaType) {
    super(hash, name, javaType);
  }
}
