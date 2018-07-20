package org.smoothbuild.lang.type;

public class TypeTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(TypesDb typesDb) {
    return typesDb.type();
  }
}
