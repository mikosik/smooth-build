package org.smoothbuild.record.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class BoolArrayTest extends TestingContext {
  @Test
  public void spec_of_bool_array_is_bool_array() {
    Array array = arrayBuilder(boolSpec()).build();
    assertThat(array.spec())
        .isEqualTo(arraySpec(boolSpec()));
  }
}
