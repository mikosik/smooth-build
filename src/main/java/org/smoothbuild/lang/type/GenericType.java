package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TypeNames.GENERIC;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public class GenericType extends Type {
  protected GenericType(HashCode dataHash, TypeType type, HashedDb hashedDb) {
    super(dataHash, type, null, GENERIC, Value.class, hashedDb);
  }

  @Override
  public Value newValue(HashCode dataHash) {
    throw new RuntimeException("Cannot create value of type '" + GENERIC + "'.");
  }
}
