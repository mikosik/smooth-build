package org.smoothbuild.record.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.type.BinaryType;

public interface SObject {
  public Hash hash();

  public Hash dataHash();

  public BinaryType type();
}
