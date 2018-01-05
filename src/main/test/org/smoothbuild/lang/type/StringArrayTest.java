package org.smoothbuild.lang.type;

public class StringArrayTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypeSystem typeSystem) {
    return typeSystem.array(typeSystem.string());
  }
}
