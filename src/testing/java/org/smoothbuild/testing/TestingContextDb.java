package org.smoothbuild.testing;

import org.smoothbuild.db.object.type.ObjTypeDb;
import org.smoothbuild.lang.base.type.api.TypeFactory;

public class TestingContextDb extends AbstractTestingContext {
  private TypeFactory typeFactory;

  @Override
  public TypeFactory typeFactory() {
    if (typeFactory == null) {
      typeFactory = new ObjTypeDb(hashedDb());
    }
    return typeFactory;
  }
}
