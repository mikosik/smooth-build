package org.smoothbuild.db.record.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.spec.Spec;

public interface Record {
  public Hash hash();

  public Hash dataHash();

  public Spec spec();

  public String valueToString();
}
