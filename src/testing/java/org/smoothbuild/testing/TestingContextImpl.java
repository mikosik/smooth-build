package org.smoothbuild.testing;

import org.smoothbuild.lang.base.type.api.TypeFactory;

public class TestingContextImpl extends AbstractTestingContext {
  @Override
  public TypeFactory typeFactory() {
    return sTypeFactory();
  }
}
