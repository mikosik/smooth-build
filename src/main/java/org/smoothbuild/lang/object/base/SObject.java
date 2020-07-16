package org.smoothbuild.lang.object.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.type.BinaryType;

public interface SObject {
  public Hash hash();

  public Hash dataHash();

  public BinaryType type();
}
