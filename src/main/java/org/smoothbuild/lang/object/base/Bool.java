package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.lang.object.type.ConcreteType;

public class Bool extends SObjectImpl {
  public Bool(Hash dataHash, ConcreteType type, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    checkArgument(type.name().equals("Bool"));
  }

  public boolean data() {
    try {
      return hashedDb.readBoolean(dataHash());
    } catch (HashedDbException e) {
      throw new ObjectsDbException(hash(), e);
    }
  }

  @Override
  public String toString() {
    return type().name() + "(" + data() + "):" + hash();
  }
}
