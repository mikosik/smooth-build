package org.smoothbuild.virtualmachine.bytecode.type.oper;

import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public abstract class OperCB extends CategoryB {
  private final TypeB evaluationType;

  protected OperCB(Hash hash, CategoryKindB kind, TypeB evaluationType) {
    super(hash, kind.name() + ":" + evaluationType.name(), kind);
    this.evaluationType = evaluationType;
  }

  public TypeB evaluationType() {
    return evaluationType;
  }
}
