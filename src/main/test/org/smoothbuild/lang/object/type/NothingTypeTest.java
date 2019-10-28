package org.smoothbuild.lang.object.type;

import org.smoothbuild.lang.object.db.ObjectsDb;

public class NothingTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ObjectsDb objectsDb) {
    return objectsDb.nothingType();
  }
}
