package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeNames.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.db.ObjectsDb;

public class StringType extends ConcreteType {
  public StringType(Hash dataHash, TypeType type, HashedDb hashedDb, ObjectsDb objectsDb) {
    super(dataHash, type, null, STRING, SString.class, hashedDb, objectsDb);
  }

  @Override
  public SString newInstance(Hash dataHash) {
    return new SString(dataHash, this, hashedDb);
  }
}
