package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.lang.object.db.ObjectsDbException.corruptedObjectException;
import static org.smoothbuild.lang.object.db.ObjectsDbException.objectsDbException;

import java.io.IOException;

import org.smoothbuild.db.hashed.DecodingStringException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ValuesDb;
import org.smoothbuild.lang.object.type.ConcreteType;

public class SString extends SObjectImpl {
  public SString(Hash dataHash, ConcreteType type, ValuesDb valuesDb) {
    super(dataHash, type, valuesDb);
    checkArgument(type.name().equals("String"));
  }

  public String data() {
    try {
      return valuesDb.readString(dataHash());
    } catch (IOException e) {
      throw objectsDbException(e);
    } catch (DecodingStringException e) {
      throw corruptedObjectException(hash(), "It is an instance of a String which data cannot be " +
          "decoded using " + CHARSET + " encoding.");
    }
  }
}
