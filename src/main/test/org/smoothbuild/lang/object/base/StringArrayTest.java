package org.smoothbuild.lang.object.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.testing.TestingContext;

public class StringArrayTest extends TestingContext {
  private Array array;

  @Test
  public void type_of_string_array_is_string_array() throws Exception {
    given(array = arrayBuilder(stringType()).build());
    when(array.type());
    thenReturned(arrayType(stringType()));
  }
}
