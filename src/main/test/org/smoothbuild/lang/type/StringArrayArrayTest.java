package org.smoothbuild.lang.type;

public class StringArrayArrayTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(TypesDb typesDb) {
    return typesDb.array(typesDb.array(typesDb.string()));
  }
}
