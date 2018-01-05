package org.smoothbuild.lang.type;

public class StringTypeTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypesDb typesDb) {
    return typesDb.string();
  }
}
