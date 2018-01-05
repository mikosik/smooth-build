package org.smoothbuild.lang.type;

public class TypeTypeTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypeSystem typeSystem) {
    return typeSystem.type();
  }
}
