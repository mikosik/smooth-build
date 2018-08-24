package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.values.ValuesDbException.readException;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ConcreteType;

import com.google.common.hash.HashCode;

public class SString extends AbstractValue {
  public SString(HashCode dataHash, ConcreteType type, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    checkArgument(type.name().equals("String"));
  }

  public String data() {
    try {
      return hashedDb.readString(dataHash());
    } catch (IOException e) {
      throw readException(e);
    }
  }
}
