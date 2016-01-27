package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.Value;

public class NothingType extends Type {
  protected NothingType() {
    super("Nothing", Nothing.class);
  }

  public Value defaultValue(ValuesDb valuesDb) {
    return null;
  }
}
