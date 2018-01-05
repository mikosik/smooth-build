package org.smoothbuild.lang.type;

public class StringArrayTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypesDb typesDb) {
    return typesDb.array(typesDb.string());
  }
}
