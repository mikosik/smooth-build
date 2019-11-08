package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.lang.object.db.ValuesDb;
import org.smoothbuild.lang.object.db.ValuesDbException;
import org.smoothbuild.lang.object.type.ConcreteType;

public class SString extends SObjectImpl {
  public SString(Hash dataHash, ConcreteType type, ValuesDb valuesDb) {
    super(dataHash, type, valuesDb);
    checkArgument(type.name().equals("String"));
  }

  public String data() {
    try {
      return valuesDb.readString(dataHash());
    } catch (ValuesDbException e) {
      throw new ObjectsDbException(hash(), e);
    }
  }
}
