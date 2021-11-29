package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class RefHTest extends TestingContext {
  @Test
  public void type_of_ref_expr_is_ref_type() {
    assertThat(refH(intHT(), 123).spec())
        .isEqualTo(refHT(intHT()));
  }

  @Test
  public void value_returns_stored_value() {
    assertThat(refH(123).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void refs_with_equal_components_are_equal() {
    assertThat(refH(intHT(), 123))
        .isEqualTo(refH(intHT(), 123));
  }

  @Test
  public void refs_with_different_pointers_are_not_equal() {
    assertThat(refH(intHT(), 123))
        .isNotEqualTo(refH(intHT(), 124));
  }

  @Test
  public void refs_with_different_evaluation_types_are_not_equal() {
    assertThat(refH(intHT(), 123))
        .isNotEqualTo(refH(stringHT(), 123));
  }

  @Test
  public void hash_of_refs_with_equal_components_is_the_same() {
    assertThat(refH(intHT(), 123).hash())
        .isEqualTo(refH(intHT(), 123).hash());
  }

  @Test
  public void hash_of_refs_with_different_pointers_is_not_the_same() {
    assertThat(refH(intHT(), 123).hash())
        .isNotEqualTo(refH(intHT(), 124).hash());
  }

  @Test
  public void hash_of_refs_with_different_evaluation_type_is_not_the_same() {
    assertThat(refH(intHT(), 123).hash())
        .isNotEqualTo(refH(stringHT(), 123).hash());
  }

  @Test
  public void hash_code_of_refs_with_equal_components_is_the_same() {
    assertThat(refH(intHT(), 123).hashCode())
        .isEqualTo(refH(intHT(), 123).hashCode());
  }

  @Test
  public void hash_code_of_refs_with_different_pointers_is_not_the_same() {
    assertThat(refH(intHT(), 123).hashCode())
        .isNotEqualTo(refH(intHT(), 124).hashCode());
  }

  @Test
  public void hash_code_of_refs_with_evaluation_types_is_not_the_same() {
    assertThat(refH(intHT(), 123).hashCode())
        .isNotEqualTo(refH(stringHT(), 123).hashCode());
  }

  @Test
  public void ref_can_be_read_back_by_hash() {
    RefH ref = refH(intHT(), 123);
    assertThat(objectHDbOther().get(ref.hash()))
        .isEqualTo(ref);
  }

  @Test
  public void const_read_back_by_hash_has_same_value() {
    RefH ref = refH(intHT(), 123);
    assertThat(((RefH) objectHDbOther().get(ref.hash())).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string() {
    RefH ref = refH(intHT(), 123);
    assertThat(ref.toString())
        .isEqualTo("Ref(123)@" + ref.hash());
  }
}
