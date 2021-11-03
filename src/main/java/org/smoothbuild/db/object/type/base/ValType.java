package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public abstract class ValType extends ObjType {
  protected ValType(String name, Hash hash, ObjKind kind) {
    super(name, hash, kind, ImmutableSet.of());
  }

  protected ValType(String name, Hash hash, ObjKind kind, ImmutableSet<Variable> variables) {
    super(name, hash, kind, variables);
  }
}
