package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.testing.TestingContext;

public class ConstTest extends TestingContext {
  @Test
  public void spec_of_const_expr_is_const_spec() {
    assertThat(constExpr(intVal(123)).spec())
        .isEqualTo(constSpec());
  }

  @Test
  public void value_returns_stored_value() {
    Val val = intVal(123);
    assertThat(constExpr(val).value())
        .isEqualTo(val);
  }

  @Test
  public void const_with_equal_values_are_equal() {
    assertThat(constExpr(intVal(123)))
        .isEqualTo(constExpr(intVal(123)));
  }

  @Test
  public void const_with_different_values_are_not_equal() {
    assertThat(constExpr(intVal(123)))
        .isNotEqualTo(constExpr(intVal(124)));
  }

  @Test
  public void hash_of_consts_with_equal_values_is_the_same() {
    assertThat(constExpr(intVal(123)).hash())
        .isEqualTo(constExpr(intVal(123)).hash());
  }

  @Test
  public void hash_of_consts_with_different_values_is_not_the_same() {
    assertThat(constExpr(intVal(123)).hash())
        .isNotEqualTo(constExpr(intVal(124)).hash());
  }

  @Test
  public void hash_code_of_const_with_equal_values_is_the_same() {
    assertThat(constExpr(intVal(123)).hashCode())
        .isEqualTo(constExpr(intVal(123)).hashCode());
  }

  @Test
  public void hash_code_of_const_with_different_values_is_not_the_same() {
    assertThat(constExpr(intVal(123)).hashCode())
        .isNotEqualTo(constExpr(intVal(321)).hashCode());
  }

  @Test
  public void const_can_be_read_back_by_hash() {
    Const constE = constExpr(intVal(123));
    assertThat(objectDbOther().get(constE.hash()))
        .isEqualTo(constE);
  }

  @Test
  public void const_read_back_by_hash_has_same_obj() {
    Const constE = constExpr(intVal(123));
    assertThat(((Const) objectDbOther().get(constE.hash())).value())
        .isEqualTo(intVal(123));
  }

  @Test
  public void to_string() {
    Const constE = constExpr(intVal(123));
    assertThat(constE.toString())
        .isEqualTo("Const(???):" + constE.hash());
  }
}
