package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.impl.TypeFactoryImpl;

public class TypeTest extends AbstractTypeTest {
  @Override
  public TypeFactory typeFactory() {
    return new TypeFactoryImpl();
  }
}
