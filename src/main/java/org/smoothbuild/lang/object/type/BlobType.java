package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeNames.BLOB;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.db.ValuesDb;

public class BlobType extends ConcreteType {
  public BlobType(Hash dataHash, TypeType type, ValuesDb valuesDb, ObjectsDb objectsDb) {
    super(dataHash, type, null, BLOB, Blob.class, valuesDb, objectsDb);
  }

  @Override
  public Blob newSObject(Hash dataHash) {
    return new Blob(dataHash, this, valuesDb);
  }
}
