package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.Value;

public class NothingType extends ConcreteType {
  public NothingType(Hash dataHash, TypeType type, HashedDb hashedDb, ValuesDb valuesDb) {
    super(dataHash, type, null, "Nothing", Nothing.class, hashedDb, valuesDb);
  }

  @Override
  public Value newValue(Hash dataHash) {
    throw new RuntimeException("Cannot create value of type 'Nothing'.");
  }
}
