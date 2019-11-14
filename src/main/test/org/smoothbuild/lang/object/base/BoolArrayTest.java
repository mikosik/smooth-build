package org.smoothbuild.lang.object.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.testing.TestingContext;

public class BoolArrayTest extends TestingContext {
  private Array array;

  @Test
  public void type_of_bool_array_is_bool_array() throws Exception {
    given(array = arrayBuilder(boolType()).build());
    when(array.type());
    thenReturned(arrayType(boolType()));
  }
}