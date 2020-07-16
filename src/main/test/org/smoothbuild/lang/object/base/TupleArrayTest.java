package org.smoothbuild.lang.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class TupleArrayTest extends TestingContext {

  @Test
  public void type_of_struct_array_is_struct_array() {
    Array array = arrayBuilder(personType()).build();
    assertThat(array.type())
        .isEqualTo(arrayType(personType()));
  }
}
