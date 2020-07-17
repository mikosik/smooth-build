package org.smoothbuild.record.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class TupleArrayTest extends TestingContext {

  @Test
  public void spec_of_tuple_array_is_tuple_array() {
    Array array = arrayBuilder(personSpec()).build();
    assertThat(array.spec())
        .isEqualTo(arraySpec(personSpec()));
  }
}
