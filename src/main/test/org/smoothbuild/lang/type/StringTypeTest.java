package org.smoothbuild.lang.type;

public class StringTypeTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypeSystem typeSystem) {
    return typeSystem.string();
  }
}
