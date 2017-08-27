package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Value;

public class NonInferableType extends Type {
  protected NonInferableType() {
    super("<NonInferable>", Value.class);
  }

  @Override
  public Value defaultValue(ValuesDb valuesDb) {
    throw new UnsupportedOperationException();
  }
}
