package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class IntTest extends TestingContext {
  @Test
  public void type_of_int_is_int_type() {
    assertThat(int_(123).type())
        .isEqualTo(intOT());
  }

  @Test
  public void jvalue_returns_java_big_integer() {
    assertThat(int_(123).jValue())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void ints_with_equal_values_are_equal() {
    assertThat(int_(123))
        .isEqualTo(int_(123));
  }

  @Test
  public void ints_with_different_values_are_not_equal() {
    assertThat(int_(123))
        .isNotEqualTo(int_(321));
  }

  @Test
  public void hash_of_ints_with_equal_values_is_the_same() {
    assertThat(int_(123).hash())
        .isEqualTo(int_(123).hash());
  }

  @Test
  public void hash_of_ints_with_different_values_is_not_the_same() {
    assertThat(int_(123).hash())
        .isNotEqualTo(int_(321).hash());
  }

  @Test
  public void hash_code_of_ints_with_equal_values_is_the_same() {
    assertThat(int_(123).hashCode())
        .isEqualTo(int_(123).hashCode());
  }

  @Test
  public void hash_code_of_ints_with_different_values_is_not_the_same() {
    assertThat(int_(123).hashCode())
        .isNotEqualTo(int_(321).hashCode());
  }

  @Test
  public void int_can_be_read_back_by_hash() {
    Int i = int_(123);
    assertThat(objectDbOther().get(i.hash()))
        .isEqualTo(i);
  }

  @Test
  public void int_read_back_by_hash_has_same_jvalue() {
    Int i = int_(123);
    assertThat(((Int) objectDbOther().get(i.hash())).jValue())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string_contains_int_value() {
    Int i = int_(123);
    assertThat(i.toString())
        .isEqualTo("123@" + i.hash());
  }
}
