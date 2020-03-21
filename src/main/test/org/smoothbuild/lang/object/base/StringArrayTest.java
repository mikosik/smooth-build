package org.smoothbuild.lang.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.smoothbuild.testing.TestingContext;

public class StringArrayTest extends TestingContext {
  @Test
  public void type_of_string_array_is_string_array() {
    assertThat(arrayBuilder(stringType()).build().type())
        .isEqualTo(arrayType(stringType()));
  }
}
