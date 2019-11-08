package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.lang.object.db.ValuesDb;
import org.smoothbuild.lang.object.db.ValuesDbException;
import org.smoothbuild.lang.object.type.ConcreteType;

public class Bool extends SObjectImpl {
  public Bool(Hash dataHash, ConcreteType type, ValuesDb valuesDb) {
    super(dataHash, type, valuesDb);
    checkArgument(type.name().equals("Bool"));
  }

  public boolean data() {
    try {
      return valuesDb.readBoolean(dataHash());
    } catch (ValuesDbException e) {
      throw new ObjectsDbException(hash(), e);
    }
  }

  @Override
  public String toString() {
    return type().name() + "(" + data() + "):" + hash();
  }
}
