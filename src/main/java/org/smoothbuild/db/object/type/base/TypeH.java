package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.val.VariableH;

import com.google.common.collect.ImmutableSet;

public abstract class TypeH extends SpecH {
  protected TypeH(String name, Hash hash, SpecKindH kind) {
    super(name, hash, kind, ImmutableSet.of());
  }

  protected TypeH(String name, Hash hash, SpecKindH kind, ImmutableSet<VariableH> variables) {
    super(name, hash, kind, variables);
  }

  @Override
  public ImmutableSet<VariableH> variables() {
    return (ImmutableSet<VariableH>) super.variables();
  }
}
