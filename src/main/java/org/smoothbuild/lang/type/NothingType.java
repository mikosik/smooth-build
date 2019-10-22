package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public class NothingType extends ConcreteType {
  public NothingType(HashCode dataHash, TypeType type, HashedDb hashedDb, ValuesDb valuesDb) {
    super(dataHash, type, null, "Nothing", Nothing.class, hashedDb, valuesDb);
  }

  @Override
  public Value newValue(HashCode dataHash) {
    throw new RuntimeException("Cannot create value of type 'Nothing'.");
  }
}
