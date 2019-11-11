package org.smoothbuild.lang.object.type;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.db.ObjectsDbException;

public class TypeType extends ConcreteType {
  public TypeType(Hash dataHash, ObjectsDb objectsDb, HashedDb hashedDb) {
    super(writeHashes(hashedDb, dataHash), dataHash, null, null, "Type", ConcreteType.class,
        hashedDb, objectsDb);
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
    throw new UnsupportedOperationException("This method in this subclass is never called as " +
        "ObjectsDb treats it as corner case so it could properly cache returned type and have its" +
        " hash when reporting error in case of problems.");
  }
}
