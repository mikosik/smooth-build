package org.smoothbuild.db.object.spec.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class ExprSpec extends Spec {
  protected ExprSpec(Hash hash, SpecKind kind) {
    super(hash, kind);
  }
}
