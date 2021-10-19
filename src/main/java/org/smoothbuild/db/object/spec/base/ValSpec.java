package org.smoothbuild.db.object.spec.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class ValSpec extends Spec {
  protected ValSpec(String name, Hash hash, SpecKind kind) {
    super(name, hash, kind);
  }
}
