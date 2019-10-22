package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;

public class TypeTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ValuesDb valuesDb) {
    return valuesDb.typeType();
  }
}
