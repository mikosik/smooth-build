package org.smoothbuild.lang.type;

import org.junit.Test;

public class GenericTypeTest extends AbstractTypeTestCase {
  @Override
  protected Type getType(TypesDb typesDb) {
    return typesDb.generic("b");
  }

  @Override
  @Test
  public void type_is_cached() throws Exception {
    // override test and make it empty
    // as generic type is not cached
  }
}
