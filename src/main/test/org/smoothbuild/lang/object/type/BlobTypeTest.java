package org.smoothbuild.lang.object.type;

import org.smoothbuild.lang.object.db.ObjectDb;

public class BlobTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ObjectDb objectDb) {
    return objectDb.blobType();
  }
}
