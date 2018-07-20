package org.smoothbuild.lang.type;

public class BlobTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(TypesDb typesDb) {
    return typesDb.blob();
  }
}
