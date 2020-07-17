package org.smoothbuild.record.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class BoolArrayTest extends TestingContext {
  @Test
  public void type_of_bool_array_is_bool_array() {
    Array array = arrayBuilder(boolType()).build();
    assertThat(array.type())
        .isEqualTo(arrayType(boolType()));
  }
}
