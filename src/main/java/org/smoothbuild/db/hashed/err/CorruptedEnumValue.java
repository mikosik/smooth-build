package org.smoothbuild.db.hashed.err;

import org.smoothbuild.db.hashed.EnumValues;

public class CorruptedEnumValue extends HashedDbError {
  public CorruptedEnumValue(EnumValues<?> enumValues, byte actualValue) {
    super("Expected enum value " + enumValues.getClass().getSimpleName() + " but got "
        + actualValue);
  }
}
