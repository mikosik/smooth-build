package org.smoothbuild.lang.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.object.type.NothingType;
import org.smoothbuild.testing.TestingContext;

public class NothingArrayTest extends TestingContext {
  @Test
  public void type_of_nothing_array_is_nothing_array() {
    Array array = emptyArrayOf(nothingType());
    assertThat(array.type())
        .isEqualTo(arrayType(nothingType()));
  }

  @Test
  public void nothing_array_is_empty() {
    assertThat(emptyArrayOf(nothingType()).asIterable(SObject.class))
        .isEmpty();
  }

  @Test
  public void nothing_array_can_be_read_by_hash() {
    Array array = emptyArrayOf(nothingType());
    assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void nothing_array_read_by_hash_is_empty() {
    Array array = emptyArrayOf(nothingType());
    assertThat(((Array) objectDbOther().get(array.hash())).asIterable(SObject.class))
        .isEmpty();
  }

  @Test
  public void nothing_array_to_string() {
    Array array = emptyArrayOf(nothingType());
    assertThat(array.toString())
        .isEqualTo("[Nothing](...):" + array.hash());
  }

  private Array emptyArrayOf(NothingType elemType) {
    return arrayBuilder(elemType).build();
  }
}
