package org.smoothbuild.record.type;

import org.smoothbuild.record.db.ObjectDb;

public class TypeTypeTest extends AbstractTypeTestCase {
  @Override
  protected BinaryType getType(ObjectDb objectDb) {
    return objectDb.typeType();
  }
}
