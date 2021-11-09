package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.testing.TestingContext;

public class ConstHTest extends TestingContext {
  @Test
  public void type_of_const_expr_is_inferred_correctly() {
    assertThat(intExpr(1).type())
        .isEqualTo(constOT(intOT()));
  }

  @Test
  public void value_returns_stored_value() {
    ValueH val = int_(1);
    assertThat(const_(val).value())
        .isEqualTo(val);
  }

  @Test
  public void const_with_equal_values_are_equal() {
    assertThat(intExpr(1))
        .isEqualTo(intExpr(1));
  }

  @Test
  public void const_with_different_values_are_not_equal() {
    assertThat(intExpr(1))
        .isNotEqualTo(intExpr(2));
  }

  @Test
  public void hash_of_consts_with_equal_values_is_the_same() {
    assertThat(intExpr(1).hash())
        .isEqualTo(intExpr(1).hash());
  }

  @Test
  public void hash_of_consts_with_different_values_is_not_the_same() {
    assertThat(intExpr(1).hash())
        .isNotEqualTo(intExpr(2).hash());
  }

  @Test
  public void hash_code_of_const_with_equal_values_is_the_same() {
    assertThat(intExpr(1).hashCode())
        .isEqualTo(intExpr(1).hashCode());
  }

  @Test
  public void hash_code_of_const_with_different_values_is_not_the_same() {
    assertThat(intExpr(1).hashCode())
        .isNotEqualTo(intExpr(2).hashCode());
  }

  @Test
  public void const_can_be_read_back_by_hash() {
    ConstH constE = intExpr(1);
    assertThat(objectDbOther().get(constE.hash()))
        .isEqualTo(constE);
  }

  @Test
  public void const_read_back_by_hash_has_same_obj() {
    ConstH constE = intExpr(1);
    assertThat(((ConstH) objectDbOther().get(constE.hash())).value())
        .isEqualTo(int_(1));
  }

  @Test
  public void to_string() {
    ConstH constE = intExpr(1);
    assertThat(constE.toString())
        .isEqualTo("Const(1)@" + constE.hash());
  }
}
