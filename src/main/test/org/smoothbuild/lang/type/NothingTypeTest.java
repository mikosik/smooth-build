package org.smoothbuild.lang.type;

public class NothingTypeTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypeSystem typeSystem) {
    return typeSystem.nothing();
  }
}
