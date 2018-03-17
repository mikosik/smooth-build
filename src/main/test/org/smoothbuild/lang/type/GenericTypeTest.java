package org.smoothbuild.lang.type;

public class GenericTypeTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypesDb typesDb) {
    return typesDb.generic();
  }
}
