package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.object.db.ObjectsDbException.corruptedObjectException;
import static org.smoothbuild.lang.object.db.ObjectsDbException.objectsDbException;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ValuesDb;
import org.smoothbuild.lang.object.type.ConcreteType;

import okio.BufferedSource;

public class Bool extends SObjectImpl {
  public Bool(Hash dataHash, ConcreteType type, ValuesDb valuesDb) {
    super(dataHash, type, valuesDb);
    checkArgument(type.name().equals("Bool"));
  }

  public boolean data() {
    try (BufferedSource source = valuesDb.source(dataHash())) {
      if (source.exhausted()) {
        throw corruptedObjectException(
            hash(), "It is Bool object which stored in ObjectsDb has zero bytes.");
      }
      byte value = source.readByte();
      if (!source.exhausted()) {
        throw corruptedObjectException(
            hash(), "It is Bool object which stored in ObjectsDb has more than one byte.");
      }
      switch (value) {
        case 0:
          return false;
        case 1:
          return true;
        default:
          throw corruptedObjectException(hash(),
              "It is Bool object which stored in ObjectsDb has illegal value (=" + value + ").");
      }
    } catch (IOException e) {
      throw objectsDbException(e);
    }
  }

  @Override
  public String toString() {
    return type().name() + "(" + data() + "):" + hash();
  }
}
