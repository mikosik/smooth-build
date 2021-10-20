package org.smoothbuild.testing;

import org.smoothbuild.db.object.spec.SpecDb;
import org.smoothbuild.lang.base.type.api.TypeFactory;

public class TestingContextDb extends AbstractTestingContext {
  private TypeFactory typeFactory;

  @Override
  public TypeFactory typeFactory() {
    if (typeFactory == null) {
      typeFactory = new SpecDb(hashedDb());
    }
    return typeFactory;
  }
}
