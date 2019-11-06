package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.db.ObjectsDbException.objectsDbException;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.db.ValuesDb;

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
