package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.lang.object.type.ConcreteType;

import okio.BufferedSource;

public class Blob extends SObjectImpl {
  public Blob(Hash dataHash, ConcreteType type, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    checkArgument(type.name().equals("Blob"));
  }

  public BufferedSource source() {
    try {
      return hashedDb.source(dataHash());
    } catch (HashedDbException e) {
      throw new ObjectsDbException(hash(), e);
    }
  }
}
