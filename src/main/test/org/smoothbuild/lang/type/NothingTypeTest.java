package org.smoothbuild.lang.type;

public class NothingTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(TypesDb typesDb) {
    return typesDb.nothing();
  }
}
