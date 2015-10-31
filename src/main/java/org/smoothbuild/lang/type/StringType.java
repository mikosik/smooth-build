package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

public class StringType extends Type {
  protected StringType() {
    super("String", SString.class);
  }

  @Override
  public Value defaultValue(ValuesDb valuesDb) {
    return valuesDb.string("");
  }

  @Override
  public boolean isAllowedAsResult() {
    return true;
  }

  @Override
  public boolean isAllowedAsParameter() {
    return true;
  }
}
