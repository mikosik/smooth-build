package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class ParamRefBTest extends TestingContext {
  @Test
  public void type_of_ref_expr_is_ref_type() {
    assertThat(paramRefB(intTB(), 123).cat())
        .isEqualTo(paramRefCB(intTB()));
  }

  @Test
  public void value_returns_stored_value() {
    assertThat(paramRefB(123).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void refs_with_equal_components_are_equal() {
    assertThat(paramRefB(intTB(), 123))
        .isEqualTo(paramRefB(intTB(), 123));
  }

  @Test
  public void refs_with_different_pointers_are_not_equal() {
    assertThat(paramRefB(intTB(), 123))
        .isNotEqualTo(paramRefB(intTB(), 124));
  }

  @Test
  public void refs_with_different_evaluation_types_are_not_equal() {
    assertThat(paramRefB(intTB(), 123))
        .isNotEqualTo(paramRefB(stringTB(), 123));
  }

  @Test
  public void hash_of_refs_with_equal_components_is_the_same() {
    assertThat(paramRefB(intTB(), 123).hash())
        .isEqualTo(paramRefB(intTB(), 123).hash());
  }

  @Test
  public void hash_of_refs_with_different_pointers_is_not_the_same() {
    assertThat(paramRefB(intTB(), 123).hash())
        .isNotEqualTo(paramRefB(intTB(), 124).hash());
  }

  @Test
  public void hash_of_refs_with_different_evaluation_type_is_not_the_same() {
    assertThat(paramRefB(intTB(), 123).hash())
        .isNotEqualTo(paramRefB(stringTB(), 123).hash());
  }

  @Test
  public void hash_code_of_refs_with_equal_components_is_the_same() {
    assertThat(paramRefB(intTB(), 123).hashCode())
        .isEqualTo(paramRefB(intTB(), 123).hashCode());
  }

  @Test
  public void hash_code_of_refs_with_different_pointers_is_not_the_same() {
    assertThat(paramRefB(intTB(), 123).hashCode())
        .isNotEqualTo(paramRefB(intTB(), 124).hashCode());
  }

  @Test
  public void hash_code_of_refs_with_evaluation_types_is_not_the_same() {
    assertThat(paramRefB(intTB(), 123).hashCode())
        .isNotEqualTo(paramRefB(stringTB(), 123).hashCode());
  }

  @Test
  public void ref_can_be_read_back_by_hash() {
    ParamRefB paramRef = paramRefB(intTB(), 123);
    assertThat(byteDbOther().get(paramRef.hash()))
        .isEqualTo(paramRef);
  }

  @Test
  public void const_read_back_by_hash_has_same_value() {
    ParamRefB paramRef = paramRefB(intTB(), 123);
    assertThat(((ParamRefB) byteDbOther().get(paramRef.hash())).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string() {
    ParamRefB paramRef = paramRefB(intTB(), 123);
    assertThat(paramRef.toString())
        .isEqualTo("ParamRef:Int(123)@" + paramRef.hash());
  }
}
