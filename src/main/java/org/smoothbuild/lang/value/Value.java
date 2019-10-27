package org.smoothbuild.lang.value;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.type.ConcreteType;

public interface Value {
  public Hash hash();

  public Hash dataHash();

  public ConcreteType type();
}
