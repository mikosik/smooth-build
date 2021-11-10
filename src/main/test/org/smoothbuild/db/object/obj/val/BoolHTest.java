package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class BoolHTest extends TestingContext {
  @Test
  public void type_of_bool_is_bool_type() {
    assertThat(bool(true).type())
        .isEqualTo(boolOT());
  }

  @Test
  public void jvalue_returns_java_true_from_true_bool() {
    BoolH bool = bool(true);
    assertThat(bool.jValue())
        .isTrue();
  }

  @Test
  public void javlue_returns_java_false_from_false_bool() {
    BoolH bool = bool(false);
    assertThat(bool.jValue())
        .isFalse();
  }

  @Test
  public void bools_with_equal_values_are_equal() {
    assertThat(bool(true))
        .isEqualTo(bool(true));
  }

  @Test
  public void bools_with_different_values_are_not_equal() {
    assertThat(bool(true))
        .isNotEqualTo(bool(false));
  }

  @Test
  public void hash_of_true_bools_are_the_same() {
    assertThat(bool(true).hash())
        .isEqualTo(bool(true).hash());
  }

  @Test
  public void hash_of_false_bools_are_the_same() {
    assertThat(bool(false).hash())
        .isEqualTo(bool(false).hash());
  }

  @Test
  public void hash_of_bools_with_different_values_is_not_the_same() {
    assertThat(bool(true).hash())
        .isNotEqualTo(bool(false).hash());
  }

  @Test
  public void hash_code_of_true_bools_is_the_same() {
    assertThat(bool(true).hashCode())
        .isEqualTo(bool(true).hashCode());
  }

  @Test
  public void hash_code_of_false_bools_is_the_same() {
    assertThat(bool(false).hashCode())
        .isEqualTo(bool(false).hashCode());
  }

  @Test
  public void hash_code_of_bools_with_different_values_is_not_the_same() {
    assertThat(bool(true).hashCode())
        .isNotEqualTo(bool(false).hashCode());
  }

  @Test
  public void bool_can_be_read_back_by_hash() {
    BoolH bool = bool(true);
    Hash hash = bool.hash();
    assertThat(objectDbOther().get(hash))
        .isEqualTo(bool);
  }

  @Test
  public void bool_read_back_by_hash_has_same_jvalue() {
    BoolH bool = bool(true);
    assertThat(((BoolH) objectDbOther().get(bool.hash())).jValue())
        .isTrue();
  }

  @Test
  public void to_string_contains_value() {
    BoolH bool = bool(true);
    assertThat(bool.toString())
        .isEqualTo("true@" + bool.hash());
  }
}