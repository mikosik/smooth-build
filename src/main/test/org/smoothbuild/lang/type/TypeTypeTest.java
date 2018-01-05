package org.smoothbuild.lang.type;

public class TypeTypeTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypesDb typesDb) {
    return typesDb.type();
  }
}
