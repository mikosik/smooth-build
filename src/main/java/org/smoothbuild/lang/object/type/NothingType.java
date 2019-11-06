package org.smoothbuild.lang.object.type;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.Nothing;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.db.ValuesDb;

public class NothingType extends ConcreteType {
  public NothingType(Hash dataHash, TypeType type, ValuesDb valuesDb, ObjectsDb objectsDb) {
    super(dataHash, type, null, "Nothing", Nothing.class, valuesDb, objectsDb);
  }

  @Override
  public SObject newInstance(Hash dataHash) {
    throw new RuntimeException("Cannot create instance of type 'Nothing'.");
  }
}
