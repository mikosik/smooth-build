package org.smoothbuild.db.object.spec.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public abstract class ValSpec extends Spec {
  protected ValSpec(String name, Hash hash, SpecKind kind) {
    super(name, hash, kind, ImmutableSet.of());
  }

  protected ValSpec(String name, Hash hash, SpecKind kind, ImmutableSet<Variable> variables) {
    super(name, hash, kind, variables);
  }
}
