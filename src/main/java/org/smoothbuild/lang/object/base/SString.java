package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.lang.object.type.ConcreteType;

public class SString extends SObjectImpl {
  public SString(Hash dataHash, ConcreteType type, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    checkArgument(type.name().equals("String"));
  }

  public String data() {
    try {
      return hashedDb.readString(dataHash());
    } catch (HashedDbException e) {
      throw new ObjectsDbException(hash(), e);
    }
  }
}
