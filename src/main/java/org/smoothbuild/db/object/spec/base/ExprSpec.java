package org.smoothbuild.db.object.spec.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;

public abstract class ExprSpec extends Spec {
  protected ExprSpec(Hash hash, SpecKind kind, ObjectDb objectDb) {
    super(hash, kind, objectDb);
  }
}
