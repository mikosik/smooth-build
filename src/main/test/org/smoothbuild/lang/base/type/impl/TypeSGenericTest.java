package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.AbstractTypeGenericTest;
import org.smoothbuild.lang.base.type.api.TypeFactory;

public class TypeSGenericTest extends AbstractTypeGenericTest<TypeS> {
  @Override
  public TypeFactory<TypeS> typeFactory() {
    return typeFactoryS();
  }
}
