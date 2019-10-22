package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;

public class StringArrayTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ValuesDb valuesDb) {
    return valuesDb.arrayType(valuesDb.stringType());
  }
}
