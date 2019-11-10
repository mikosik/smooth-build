package org.smoothbuild.lang.object.type;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.db.ObjectsDbException;

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
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }

  @Override
  public ConcreteType type() {
    return this;
  }

  @Override
  public ConcreteType newSObject(Hash dataHash) {
    return objectsDb.readFromDataHash(dataHash, hash());
  }
}
