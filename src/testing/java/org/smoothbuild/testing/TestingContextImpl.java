package org.smoothbuild.testing;

import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.impl.TypeFactoryImpl;

public class TestingContextImpl extends AbstractTestingContext {
  private TypeFactory typeFactory;

  @Override
  public TypeFactory typeFactory() {
    if (typeFactory == null) {
      typeFactory = new TypeFactoryImpl();
    }
    return typeFactory;
  }
}
