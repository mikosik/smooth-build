package org.smoothbuild.lang.type;

public class BlobTypeTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypesDb typesDb) {
    return typesDb.blob();
  }
}
