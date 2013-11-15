package org.smoothbuild.io.db.hash.err;

import org.smoothbuild.io.db.hash.EnumValues;

public class CorruptedEnumValue extends HashedDbError {
  public CorruptedEnumValue(EnumValues<?> enumValues, byte actualValue) {
    super("Expected enum value " + enumValues.getClass().getSimpleName() + " but got "
        + actualValue);
  }
}
