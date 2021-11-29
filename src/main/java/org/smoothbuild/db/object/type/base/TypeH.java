package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.val.VarH;

import com.google.common.collect.ImmutableSet;

public abstract class TypeH extends SpecH {
  protected TypeH(String name, Hash hash, SpecKindH kind) {
    super(name, hash, kind, ImmutableSet.of());
  }

  protected TypeH(String name, Hash hash, SpecKindH kind, ImmutableSet<VarH> vars) {
    super(name, hash, kind, vars);
  }

  @Override
  public ImmutableSet<VarH> vars() {
    return (ImmutableSet<VarH>) super.vars();
  }
}
