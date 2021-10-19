package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.AbstractTypeTest;
import org.smoothbuild.lang.base.type.api.TypeFactory;

public class TypeTest extends AbstractTypeTest {
  @Override
  public TypeFactory typeFactory() {
    return new TypeFactoryImpl();
  }
}
