package org.smoothbuild.lang.type;

public class StringTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(TypesDb typesDb) {
    return typesDb.string();
  }
}
