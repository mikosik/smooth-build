package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;

public class StringArrayArrayTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ValuesDb valuesDb) {
    return valuesDb.arrayType(valuesDb.arrayType(valuesDb.stringType()));
  }
}
