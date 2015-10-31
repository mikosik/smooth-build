package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.Value;

public class NothingType extends Type {
  protected NothingType() {
    super("Nothing", Nothing.class);
  }

  @Override
  public Value defaultValue(ValuesDb valuesDb) {
    throw new UnsupportedOperationException("Nothing type doesn't have default value.");
  }

  @Override
  public boolean isAllowedAsResult() {
    return false;
  }

  @Override
  public boolean isAllowedAsParameter() {
    return false;
  }
}
