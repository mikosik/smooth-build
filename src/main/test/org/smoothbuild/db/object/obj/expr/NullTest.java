package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class NullTest extends TestingContext {
  @Test
  public void spec_of_null_expr_is_null_spec() {
    assertThat(nullExpr().spec())
        .isEqualTo(nullSpec());
  }

  @Test
  public void data_hash_is_null() {
    assertThat(nullExpr().dataHash())
        .isNull();
  }

  @Test
  public void nulls_are_equal() {
    assertThat(nullExpr())
        .isEqualTo(nullExpr());
  }

  @Test
  public void hash_of_nulls_is_the_same() {
    assertThat(nullExpr().hash())
        .isEqualTo(nullExpr().hash());
  }

  @Test
  public void hash_code_of_nulls_is_the_same() {
    assertThat(nullExpr().hashCode())
        .isEqualTo(nullExpr().hashCode());
  }

  @Test
  public void null_can_be_read_back_by_hash() {
    Null nullExpr = nullExpr();
    assertThat(objectDbOther().get(nullExpr.hash()))
        .isEqualTo(nullExpr);
  }

  @Test
  public void to_string() {
    Null nullExpr = nullExpr();
    assertThat(nullExpr.toString())
        .isEqualTo("Null:" + nullExpr.hash());
  }
}
