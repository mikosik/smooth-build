package org.smoothbuild.lang.object.base;

import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.lang.object.type.AbstractTypeTestCase;
import org.smoothbuild.lang.object.type.ConcreteType;

public class StringArrayArrayTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ObjectDb objectDb) {
    return objectDb.arrayType(objectDb.arrayType(objectDb.stringType()));
  }
}
