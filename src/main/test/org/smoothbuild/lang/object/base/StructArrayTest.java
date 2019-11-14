package org.smoothbuild.lang.object.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.testing.TestingContext;

public class StructArrayTest extends TestingContext {
  private Array array;

  @Test
  public void type_of_struct_array_is_struct_array() throws Exception {
    given(array = arrayBuilder(personType()).build());
    when(array.type());
    thenReturned(arrayType(personType()));
  }
}
