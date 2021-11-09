package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public abstract class TypeHV extends TypeH {
  protected TypeHV(String name, Hash hash, TypeKindH kind) {
    super(name, hash, kind, ImmutableSet.of());
  }

  protected TypeHV(String name, Hash hash, TypeKindH kind, ImmutableSet<Variable> variables) {
    super(name, hash, kind, variables);
  }
}
