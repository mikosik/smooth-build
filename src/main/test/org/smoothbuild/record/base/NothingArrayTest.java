package org.smoothbuild.record.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.record.spec.NothingSpec;
import org.smoothbuild.testing.TestingContext;

public class NothingArrayTest extends TestingContext {
  @Test
  public void spec_of_nothing_array_is_nothing_array() {
    Array array = emptyArrayOf(nothingSpec());
    assertThat(array.spec())
        .isEqualTo(arraySpec(nothingSpec()));
  }

  @Test
  public void nothing_array_is_empty() {
    assertThat(emptyArrayOf(nothingSpec()).asIterable(Record.class))
        .isEmpty();
  }

  @Test
  public void nothing_array_can_be_read_by_hash() {
    Array array = emptyArrayOf(nothingSpec());
    assertThat(recordDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void nothing_array_read_by_hash_is_empty() {
    Array array = emptyArrayOf(nothingSpec());
    assertThat(((Array) recordDbOther().get(array.hash())).asIterable(Record.class))
        .isEmpty();
  }

  @Test
  public void nothing_array_to_string() {
    Array array = emptyArrayOf(nothingSpec());
    assertThat(array.toString())
        .isEqualTo("[]:" + array.hash());
  }

  private Array emptyArrayOf(NothingSpec elemSpec) {
    return arrayBuilder(elemSpec).build();
  }
}
