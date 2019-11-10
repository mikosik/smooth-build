package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeNames.BOOL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.db.ObjectsDb;

public class BoolType extends ConcreteType {
  public BoolType(Hash dataHash, TypeType type, HashedDb hashedDb, ObjectsDb objectsDb) {
    super(dataHash, type, null, BOOL, Bool.class, hashedDb, objectsDb);
  }

  @Override
  public Bool newSObject(Hash dataHash) {
    return new Bool(dataHash, this, hashedDb);
  }
}
