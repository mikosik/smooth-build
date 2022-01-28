package org.smoothbuild.bytecode.type.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.Hash;

public abstract class ExprCatB extends CatB {
  private final TypeB evalT;

  protected ExprCatB(Hash hash, String name, CatKindB kind, TypeB evalT) {
    super(hash, name + ":" + evalT.name(), kind);
    this.evalT = evalT;
    checkArgument(!evalT.hasOpenVars());
  }

  public TypeB evalT() {
    return evalT;
  }
}
