package org.smoothbuild.lang.type;

public class NothingTypeTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypesDb typesDb) {
    return typesDb.nothing();
  }
}
