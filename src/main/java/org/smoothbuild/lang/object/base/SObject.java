package org.smoothbuild.lang.object.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.type.ConcreteType;

public interface SObject {
  public Hash hash();

  public Hash dataHash();

  public ConcreteType type();
}
