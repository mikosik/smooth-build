package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class RefTest extends TestingContext {
  @Test
  public void spec_of_ref_expr_is_ref_spec() {
    assertThat(refExpr(123).spec())
        .isEqualTo(refSpec());
  }

  @Test
  public void value_returns_stored_value() {
    assertThat(refExpr(123).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void refs_with_equal_values_are_equal() {
    assertThat(refExpr(123))
        .isEqualTo(refExpr(123));
  }

  @Test
  public void refs_with_different_values_are_not_equal() {
    assertThat(refExpr(123))
        .isNotEqualTo(refExpr(124));
  }

  @Test
  public void hash_of_refs_with_equal_values_is_the_same() {
    assertThat(refExpr(123).hash())
        .isEqualTo(refExpr(123).hash());
  }

  @Test
  public void hash_of_refs_with_different_values_is_not_the_same() {
    assertThat(refExpr(123).hash())
        .isNotEqualTo(refExpr(124).hash());
  }

  @Test
  public void hash_code_of_refs_with_equal_values_is_the_same() {
    assertThat(refExpr(123).hashCode())
        .isEqualTo(refExpr(123).hashCode());
  }

  @Test
  public void hash_code_of_refs_with_different_values_is_not_the_same() {
    assertThat(refExpr(123).hashCode())
        .isNotEqualTo(refExpr(124).hashCode());
  }

  @Test
  public void ref_can_be_read_back_by_hash() {
    Ref ref = refExpr(123);
    assertThat(objectDbOther().get(ref.hash()))
        .isEqualTo(ref);
  }

  @Test
  public void const_read_back_by_hash_has_same_value() {
    Ref ref = refExpr(123);
    assertThat(((Ref) objectDbOther().get(ref.hash())).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string() {
    Ref ref = refExpr(123);
    assertThat(ref.toString())
        .isEqualTo("Ref(123):" + ref.hash());
  }
}
