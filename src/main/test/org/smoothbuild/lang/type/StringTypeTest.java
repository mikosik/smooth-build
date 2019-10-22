package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;

public class StringTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ValuesDb valuesDb) {
    return valuesDb.stringType();
  }
}
