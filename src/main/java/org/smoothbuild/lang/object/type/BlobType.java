package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeNames.BLOB;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.db.ObjectsDb;

public class BlobType extends ConcreteType {
  public BlobType(Hash dataHash, TypeType type, HashedDb hashedDb, ObjectsDb objectsDb) {
    super(dataHash, type, null, BLOB, Blob.class, hashedDb, objectsDb);
  }

  @Override
  public Blob newInstance(Hash dataHash) {
    return new Blob(dataHash, this, hashedDb);
  }
}
