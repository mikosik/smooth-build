package org.smoothbuild.lang.type;

public class StringArrayArrayTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypeSystem typeSystem) {
    return typeSystem.array(typeSystem.array(typeSystem.string()));
  }
}
