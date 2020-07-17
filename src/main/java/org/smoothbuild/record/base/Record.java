package org.smoothbuild.record.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.spec.Spec;

public interface Record {
  public Hash hash();

  public Hash dataHash();

  public Spec spec();
}
