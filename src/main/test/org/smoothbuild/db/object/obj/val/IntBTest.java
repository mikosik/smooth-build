package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class IntBTest extends TestingContext {
  @Test
  public void type_of_int_is_int_type() {
    assertThat(intB(123).cat())
        .isEqualTo(intTB());
  }

  @Test
  public void to_j_returns_java_big_integer() {
    assertThat(intB(123).toJ())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void ints_with_equal_values_are_equal() {
    assertThat(intB(123))
        .isEqualTo(intB(123));
  }

  @Test
  public void ints_with_different_values_are_not_equal() {
    assertThat(intB(123))
        .isNotEqualTo(intB(321));
  }

  @Test
  public void hash_of_ints_with_equal_values_is_the_same() {
    assertThat(intB(123).hash())
        .isEqualTo(intB(123).hash());
  }

  @Test
  public void hash_of_ints_with_different_values_is_not_the_same() {
    assertThat(intB(123).hash())
        .isNotEqualTo(intB(321).hash());
  }

  @Test
  public void hash_code_of_ints_with_equal_values_is_the_same() {
    assertThat(intB(123).hashCode())
        .isEqualTo(intB(123).hashCode());
  }

  @Test
  public void hash_code_of_ints_with_different_values_is_not_the_same() {
    assertThat(intB(123).hashCode())
        .isNotEqualTo(intB(321).hashCode());
  }

  @Test
  public void int_can_be_read_back_by_hash() {
    IntB i = intB(123);
    assertThat(byteDbOther().get(i.hash()))
        .isEqualTo(i);
  }

  @Test
  public void int_read_back_by_hash_has_same_to_J() {
    IntB i = intB(123);
    assertThat(((IntB) byteDbOther().get(i.hash())).toJ())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string_contains_int_value() {
    IntB i = intB(123);
    assertThat(i.toString())
        .isEqualTo("123@" + i.hash());
  }
}
