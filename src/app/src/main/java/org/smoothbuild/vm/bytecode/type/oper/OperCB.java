package org.smoothbuild.vm.bytecode.type.oper;

import org.smoothbuild.common.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public abstract class OperCB extends CategoryB {
  private final TypeB evaluationT;

  protected OperCB(Hash hash, CategoryKindB kind, TypeB evaluationT) {
    super(hash, kind.name() + ":" + evaluationT.name(), kind);
    this.evaluationT = evaluationT;
  }

  public TypeB evaluationT() {
    return evaluationT;
  }
}
