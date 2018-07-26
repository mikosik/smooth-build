package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public class NothingType extends ConcreteType {
  protected NothingType(HashCode dataHash, TypeType type, HashedDb hashedDb, TypesDb typesDb) {
    super(dataHash, type, null, "Nothing", Nothing.class, hashedDb, typesDb);
  }

  @Override
  public Value newValue(HashCode dataHash) {
    throw new RuntimeException("Cannot create value of type 'Nothing'.");
  }
}
