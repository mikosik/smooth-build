package org.smoothbuild.vm.bytecode.type.oper;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public abstract class OperCB extends CategoryB {
  private final TypeB evalT;

  protected OperCB(Hash hash, CategoryKindB kind, TypeB evalT) {
    super(hash, kind.name() + ":" + evalT.name(), kind);
    this.evalT = evalT;
  }

  public TypeB evalT() {
    return evalT;
  }
}
