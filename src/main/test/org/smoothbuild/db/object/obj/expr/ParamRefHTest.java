package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class ParamRefHTest extends TestingContext {
  @Test
  public void type_of_ref_expr_is_ref_type() {
    assertThat(paramRefH(intTH(), 123).cat())
        .isEqualTo(paramRefCH(intTH()));
  }

  @Test
  public void value_returns_stored_value() {
    assertThat(paramRefH(123).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void refs_with_equal_components_are_equal() {
    assertThat(paramRefH(intTH(), 123))
        .isEqualTo(paramRefH(intTH(), 123));
  }

  @Test
  public void refs_with_different_pointers_are_not_equal() {
    assertThat(paramRefH(intTH(), 123))
        .isNotEqualTo(paramRefH(intTH(), 124));
  }

  @Test
  public void refs_with_different_evaluation_types_are_not_equal() {
    assertThat(paramRefH(intTH(), 123))
        .isNotEqualTo(paramRefH(stringTH(), 123));
  }

  @Test
  public void hash_of_refs_with_equal_components_is_the_same() {
    assertThat(paramRefH(intTH(), 123).hash())
        .isEqualTo(paramRefH(intTH(), 123).hash());
  }

  @Test
  public void hash_of_refs_with_different_pointers_is_not_the_same() {
    assertThat(paramRefH(intTH(), 123).hash())
        .isNotEqualTo(paramRefH(intTH(), 124).hash());
  }

  @Test
  public void hash_of_refs_with_different_evaluation_type_is_not_the_same() {
    assertThat(paramRefH(intTH(), 123).hash())
        .isNotEqualTo(paramRefH(stringTH(), 123).hash());
  }

  @Test
  public void hash_code_of_refs_with_equal_components_is_the_same() {
    assertThat(paramRefH(intTH(), 123).hashCode())
        .isEqualTo(paramRefH(intTH(), 123).hashCode());
  }

  @Test
  public void hash_code_of_refs_with_different_pointers_is_not_the_same() {
    assertThat(paramRefH(intTH(), 123).hashCode())
        .isNotEqualTo(paramRefH(intTH(), 124).hashCode());
  }

  @Test
  public void hash_code_of_refs_with_evaluation_types_is_not_the_same() {
    assertThat(paramRefH(intTH(), 123).hashCode())
        .isNotEqualTo(paramRefH(stringTH(), 123).hashCode());
  }

  @Test
  public void ref_can_be_read_back_by_hash() {
    ParamRefH paramRef = paramRefH(intTH(), 123);
    assertThat(objDbOther().get(paramRef.hash()))
        .isEqualTo(paramRef);
  }

  @Test
  public void const_read_back_by_hash_has_same_value() {
    ParamRefH paramRef = paramRefH(intTH(), 123);
    assertThat(((ParamRefH) objDbOther().get(paramRef.hash())).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string() {
    ParamRefH paramRef = paramRefH(intTH(), 123);
    assertThat(paramRef.toString())
        .isEqualTo("ParamRef:Int(123)@" + paramRef.hash());
  }
}
