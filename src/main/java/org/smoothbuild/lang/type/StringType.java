package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.SString;

public class StringType extends Type {
  protected StringType() {
    super("String", SString.class);
  }

  @Override
  public SString defaultValue(ValuesDb valuesDb) {
    return valuesDb.string("");
  }
}
