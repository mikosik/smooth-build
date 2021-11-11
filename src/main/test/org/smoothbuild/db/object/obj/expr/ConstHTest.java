package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.testing.TestingContext;

public class ConstHTest extends TestingContext {
  @Test
  public void type_of_const_expr_is_inferred_correctly() {
    assertThat(intHE(1).type())
        .isEqualTo(constHT(intHT()));
  }

  @Test
  public void value_returns_stored_value() {
    ValueH val = intH(1);
    assertThat(constH(val).value())
        .isEqualTo(val);
  }

  @Test
  public void const_with_equal_values_are_equal() {
    assertThat(intHE(1))
        .isEqualTo(intHE(1));
  }

  @Test
  public void const_with_different_values_are_not_equal() {
    assertThat(intHE(1))
        .isNotEqualTo(intHE(2));
  }

  @Test
  public void hash_of_consts_with_equal_values_is_the_same() {
    assertThat(intHE(1).hash())
        .isEqualTo(intHE(1).hash());
  }

  @Test
  public void hash_of_consts_with_different_values_is_not_the_same() {
    assertThat(intHE(1).hash())
        .isNotEqualTo(intHE(2).hash());
  }

  @Test
  public void hash_code_of_const_with_equal_values_is_the_same() {
    assertThat(intHE(1).hashCode())
        .isEqualTo(intHE(1).hashCode());
  }

  @Test
  public void hash_code_of_const_with_different_values_is_not_the_same() {
    assertThat(intHE(1).hashCode())
        .isNotEqualTo(intHE(2).hashCode());
  }

  @Test
  public void const_can_be_read_back_by_hash() {
    ConstH constE = intHE(1);
    assertThat(objectHDbOther().get(constE.hash()))
        .isEqualTo(constE);
  }

  @Test
  public void const_read_back_by_hash_has_same_obj() {
    ConstH constE = intHE(1);
    assertThat(((ConstH) objectHDbOther().get(constE.hash())).value())
        .isEqualTo(intH(1));
  }

  @Test
  public void to_string() {
    ConstH constE = intHE(1);
    assertThat(constE.toString())
        .isEqualTo("Const(1)@" + constE.hash());
  }
}
