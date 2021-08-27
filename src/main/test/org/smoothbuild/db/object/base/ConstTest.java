package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class ConstTest extends TestingContext {
  @Test
  public void spec_of_const_expr_is_const_expr() {
    assertThat(constE(intV(123)).spec())
        .isEqualTo(constS());
  }

  @Test
  public void value_returns_stored_value() {
    Val val = intV(123);
    assertThat(constE(val).value())
        .isEqualTo(val);
  }

  @Test
  public void const_with_equal_values_are_equal() {
    assertThat(constE(intV(123)))
        .isEqualTo(constE(intV(123)));
  }

  @Test
  public void const_with_different_values_are_not_equal() {
    assertThat(constE(intV(123)))
        .isNotEqualTo(constE(intV(124)));
  }

  @Test
  public void hash_of_consts_with_equal_values_is_the_same() {
    assertThat(constE(intV(123)).hash())
        .isEqualTo(constE(intV(123)).hash());
  }

  @Test
  public void hash_of_consts_with_different_values_is_not_the_same() {
    assertThat(constE(intV(123)).hash())
        .isNotEqualTo(constE(intV(124)).hash());
  }

  @Test
  public void hash_code_of_const_with_equal_values_is_the_same() {
    assertThat(constE(intV(123)).hashCode())
        .isEqualTo(constE(intV(123)).hashCode());
  }

  @Test
  public void hash_code_of_const_with_different_values_is_not_the_same() {
    assertThat(constE(intV(123)).hashCode())
        .isNotEqualTo(constE(intV(321)).hashCode());
  }

  @Test
  public void const_can_be_read_back_by_hash() {
    Const constE = constE(intV(123));
    assertThat(objectDbOther().get(constE.hash()))
        .isEqualTo(constE);
  }

  @Test
  public void const_read_back_by_hash_has_same_obj() {
    Const constE = constE(intV(123));
    assertThat(((Const) objectDbOther().get(constE.hash())).value())
        .isEqualTo(intV(123));
  }

  @Test
  public void to_string() {
    Const constE = constE(intV(123));
    assertThat(constE.toString())
        .isEqualTo("Const(???):" + constE.hash());
  }
}
