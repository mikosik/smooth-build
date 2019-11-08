package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeNames.BOOL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.db.ValuesDb;

public class BoolType extends ConcreteType {
  public BoolType(Hash dataHash, TypeType type, ValuesDb valuesDb, ObjectsDb objectsDb) {
    super(dataHash, type, null, BOOL, Bool.class, valuesDb, objectsDb);
  }

  @Override
  public Bool newSObject(Hash dataHash) {
    return new Bool(dataHash, this, valuesDb);
  }
}
