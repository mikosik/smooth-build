package org.smoothbuild.record.type;

import org.smoothbuild.record.db.ObjectDb;

public class StringArrayArrayTypeTest extends AbstractTypeTestCase {
  @Override
  protected BinaryType getType(ObjectDb objectDb) {
    return objectDb.arrayType(objectDb.arrayType(objectDb.stringType()));
  }
}
