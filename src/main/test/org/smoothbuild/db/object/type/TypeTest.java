package org.smoothbuild.db.object.type;

import org.smoothbuild.lang.base.type.AbstractTypeTest;
import org.smoothbuild.lang.base.type.api.TypeFactory;

public class TypeTest extends AbstractTypeTest {
  @Override
  public TypeFactory typeFactory() {
    return objTypeDb();
  }
}
