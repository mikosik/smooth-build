package org.smoothbuild.db.object.spec.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;

public abstract class ValSpec extends Spec {
  protected ValSpec(Hash hash, SpecKind kind, ObjectDb objectDb) {
    super(hash, kind, objectDb);
  }
}
