package org.smoothbuild.bytecode.type.oper;

import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.bytecode.type.CatKindB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.db.Hash;

public abstract class OperCatB extends CatB {
  private final TypeB evalT;

  protected OperCatB(Hash hash, String name, CatKindB kind, TypeB evalT) {
    super(hash, name + ":" + evalT.name(), kind);
    this.evalT = evalT;
  }

  public TypeB evalT() {
    return evalT;
  }
}
