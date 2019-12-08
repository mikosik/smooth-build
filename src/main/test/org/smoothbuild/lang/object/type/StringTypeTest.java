package org.smoothbuild.lang.object.type;

import org.smoothbuild.lang.object.db.ObjectDb;

public class StringTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ObjectDb objectDb) {
    return objectDb.stringType();
  }
}
