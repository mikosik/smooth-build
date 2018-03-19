package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public class GenericType extends Type {
  protected GenericType(HashCode dataHash, TypeType type, String name, HashedDb hashedDb) {
    super(dataHash, type, null, name, Value.class, hashedDb);
  }

  @Override
  public Value newValue(HashCode dataHash) {
    throw new RuntimeException("Cannot create value of type '" + name() + "'.");
  }
}
