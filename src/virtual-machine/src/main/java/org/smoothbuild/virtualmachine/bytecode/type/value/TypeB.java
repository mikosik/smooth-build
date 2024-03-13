package org.smoothbuild.virtualmachine.bytecode.type.value;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;

public abstract class TypeB extends CategoryB {
  protected TypeB(Hash hash, String name, Class<? extends ExprB> javaType) {
    super(hash, name, javaType);
  }
}
