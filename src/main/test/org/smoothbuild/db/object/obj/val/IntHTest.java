package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class IntHTest extends TestingContext {
  @Test
  public void type_of_int_is_int_type() {
    assertThat(intH(123).cat())
        .isEqualTo(intTH());
  }

  @Test
  public void to_j_returns_java_big_integer() {
    assertThat(intH(123).toJ())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void ints_with_equal_values_are_equal() {
    assertThat(intH(123))
        .isEqualTo(intH(123));
  }

  @Test
  public void ints_with_different_values_are_not_equal() {
    assertThat(intH(123))
        .isNotEqualTo(intH(321));
  }

  @Test
  public void hash_of_ints_with_equal_values_is_the_same() {
    assertThat(intH(123).hash())
        .isEqualTo(intH(123).hash());
  }

  @Test
  public void hash_of_ints_with_different_values_is_not_the_same() {
    assertThat(intH(123).hash())
        .isNotEqualTo(intH(321).hash());
  }

  @Test
  public void hash_code_of_ints_with_equal_values_is_the_same() {
    assertThat(intH(123).hashCode())
        .isEqualTo(intH(123).hashCode());
  }

  @Test
  public void hash_code_of_ints_with_different_values_is_not_the_same() {
    assertThat(intH(123).hashCode())
        .isNotEqualTo(intH(321).hashCode());
  }

  @Test
  public void int_can_be_read_back_by_hash() {
    IntH i = intH(123);
    assertThat(objDbOther().get(i.hash()))
        .isEqualTo(i);
  }

  @Test
  public void int_read_back_by_hash_has_same_to_J() {
    IntH i = intH(123);
    assertThat(((IntH) objDbOther().get(i.hash())).toJ())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string_contains_int_value() {
    IntH i = intH(123);
    assertThat(i.toString())
        .isEqualTo("123@" + i.hash());
  }
}
