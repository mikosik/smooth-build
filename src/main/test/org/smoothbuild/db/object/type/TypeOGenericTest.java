package org.smoothbuild.db.object.type;

import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.lang.base.type.AbstractTypeGenericTest;
import org.smoothbuild.lang.base.type.api.TypeFactory;

public class TypeOGenericTest extends AbstractTypeGenericTest<TypeV> {
  @Override
  public TypeFactory<TypeV> typeFactory() {
    return typeFactoryO();
  }
}
