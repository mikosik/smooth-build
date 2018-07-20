package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ConcreteType;

import com.google.common.hash.HashCode;

public class SString extends Value {
  public SString(HashCode dataHash, ConcreteType type, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    checkArgument(type.name().equals("String"));
  }

  public String data() {
    return hashedDb.readString(dataHash());
  }
}
