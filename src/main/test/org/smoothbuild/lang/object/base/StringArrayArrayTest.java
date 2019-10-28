package org.smoothbuild.lang.object.base;

import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.type.AbstractTypeTestCase;
import org.smoothbuild.lang.object.type.ConcreteType;

public class StringArrayArrayTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ObjectsDb objectsDb) {
    return objectsDb.arrayType(objectsDb.arrayType(objectsDb.stringType()));
  }
}
