package org.smoothbuild.lang.type;

public class BoolTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(TypesDb typesDb) {
    return typesDb.bool();
  }
}