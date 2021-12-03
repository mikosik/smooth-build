package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class BoolHTest extends TestingContext {
  @Test
  public void type_of_bool_is_bool_type() {
    assertThat(boolH(true).cat())
        .isEqualTo(boolTH());
  }

  @Test
  public void to_j_returns_java_true_from_true_bool() {
    BoolH bool = boolH(true);
    assertThat(bool.toJ())
        .isTrue();
  }

  @Test
  public void javlue_returns_java_false_from_false_bool() {
    BoolH bool = boolH(false);
    assertThat(bool.toJ())
        .isFalse();
  }

  @Test
  public void bools_with_equal_values_are_equal() {
    assertThat(boolH(true))
        .isEqualTo(boolH(true));
  }

  @Test
  public void bools_with_different_values_are_not_equal() {
    assertThat(boolH(true))
        .isNotEqualTo(boolH(false));
  }

  @Test
  public void hash_of_true_bools_are_the_same() {
    assertThat(boolH(true).hash())
        .isEqualTo(boolH(true).hash());
  }

  @Test
  public void hash_of_false_bools_are_the_same() {
    assertThat(boolH(false).hash())
        .isEqualTo(boolH(false).hash());
  }

  @Test
  public void hash_of_bools_with_different_values_is_not_the_same() {
    assertThat(boolH(true).hash())
        .isNotEqualTo(boolH(false).hash());
  }

  @Test
  public void hash_code_of_true_bools_is_the_same() {
    assertThat(boolH(true).hashCode())
        .isEqualTo(boolH(true).hashCode());
  }

  @Test
  public void hash_code_of_false_bools_is_the_same() {
    assertThat(boolH(false).hashCode())
        .isEqualTo(boolH(false).hashCode());
  }

  @Test
  public void hash_code_of_bools_with_different_values_is_not_the_same() {
    assertThat(boolH(true).hashCode())
        .isNotEqualTo(boolH(false).hashCode());
  }

  @Test
  public void bool_can_be_read_back_by_hash() {
    BoolH bool = boolH(true);
    Hash hash = bool.hash();
    assertThat(objDbOther().get(hash))
        .isEqualTo(bool);
  }

  @Test
  public void bool_read_back_by_hash_has_same_to_j() {
    BoolH bool = boolH(true);
    assertThat(((BoolH) objDbOther().get(bool.hash())).toJ())
        .isTrue();
  }

  @Test
  public void to_string_contains_value() {
    BoolH bool = boolH(true);
    assertThat(bool.toString())
        .isEqualTo("true@" + bool.hash());
  }
}
