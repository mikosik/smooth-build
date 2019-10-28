package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.db.ObjectsDbException.objectsDbException;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.db.ObjectsDb;

public class TypeType extends ConcreteType {
  private final ObjectsDb objectsDb;

  public TypeType(Hash dataHash, ObjectsDb objectsDb, HashedDb hashedDb) {
    super(writeHashes(hashedDb, dataHash), dataHash, null, null, "Type", ConcreteType.class,
        hashedDb, objectsDb);
    this.objectsDb = objectsDb;
  }

  private static Hash writeHashes(HashedDb hashedDb, Hash dataHash) {
    try {
      return hashedDb.writeHashes(dataHash);
    } catch (IOException e) {
      throw objectsDbException(e);
    }
  }

  @Override
  public ConcreteType type() {
    return this;
  }

  @Override
  public ConcreteType newInstance(Hash dataHash) {
    try {
      return objectsDb.readFromDataHash(dataHash, hash());
    } catch (IOException e) {
      throw objectsDbException(e);
    }
  }
}
