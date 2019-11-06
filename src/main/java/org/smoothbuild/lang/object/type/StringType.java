package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeNames.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.db.ValuesDb;

public class StringType extends ConcreteType {
  public StringType(Hash dataHash, TypeType type, ValuesDb valuesDb, ObjectsDb objectsDb) {
    super(dataHash, type, null, STRING, SString.class, valuesDb, objectsDb);
  }

  @Override
  public SString newInstance(Hash dataHash) {
    return new SString(dataHash, this, valuesDb);
  }
}
