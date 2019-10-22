package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;

public class BlobTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ValuesDb valuesDb) {
    return valuesDb.blobType();
  }
}
