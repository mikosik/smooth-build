package org.smoothbuild.lang.object.type;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.Nothing;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.db.ObjectsDb;

public class NothingType extends ConcreteType {
  public NothingType(Hash dataHash, TypeType type, HashedDb hashedDb, ObjectsDb objectsDb) {
    super(dataHash, type, null, "Nothing", Nothing.class, hashedDb, objectsDb);
  }

  @Override
  public SObject newSObject(Hash dataHash) {
    throw new RuntimeException("Cannot create instance of type 'Nothing'.");
  }
}
