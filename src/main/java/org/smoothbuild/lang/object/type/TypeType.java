package org.smoothbuild.lang.object.type;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.lang.object.db.ValuesDb;
import org.smoothbuild.lang.object.db.ValuesDbException;

public class TypeType extends ConcreteType {
  private final ObjectsDb objectsDb;

  public TypeType(Hash dataHash, ObjectsDb objectsDb, ValuesDb valuesDb) {
    super(writeHashes(valuesDb, dataHash), dataHash, null, null, "Type", ConcreteType.class,
        valuesDb, objectsDb);
    this.objectsDb = objectsDb;
  }

  private static Hash writeHashes(ValuesDb valuesDb, Hash dataHash) {
    try {
      return valuesDb.writeHashes(dataHash);
    } catch (ValuesDbException e) {
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
