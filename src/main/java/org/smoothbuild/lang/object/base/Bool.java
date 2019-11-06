package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ValuesDb;
import org.smoothbuild.lang.object.type.ConcreteType;

public class Bool extends SObjectImpl {
  public Bool(Hash dataHash, ConcreteType type, ValuesDb valuesDb) {
    super(dataHash, type, valuesDb);
    checkArgument(type.name().equals("Bool"));
  }

  public boolean data() {
    return valuesDb.readBoolean(hash(), dataHash());
  }

  @Override
  public String toString() {
    return type().name() + "(" + data() + "):" + hash();
  }
}
