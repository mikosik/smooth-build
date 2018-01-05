package org.smoothbuild.lang.type;

public class BlobTypeTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypeSystem typeSystem) {
    return typeSystem.blob();
  }
}
