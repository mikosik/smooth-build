package org.smoothbuild.db.hashed.err;

import org.smoothbuild.db.hashed.EnumValues;

public class CorruptedEnumException extends HashedDbException {
  public CorruptedEnumException(EnumValues<?> enumValues, byte actualValue) {
    super("Expected enum value " + enumValues.getClass().getSimpleName() + " but got "
        + actualValue);
  }
}
