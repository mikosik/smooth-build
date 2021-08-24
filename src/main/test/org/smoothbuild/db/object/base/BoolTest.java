package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class BoolTest extends TestingContext {
  @Test
  public void spec_of_bool_is_bool() {
    assertThat(boolV(true).spec())
        .isEqualTo(boolS());
  }

  @Test
  public void jvalue_returns_java_true_from_true_bool() {
    Bool bool = boolV(true);
    assertThat(bool.jValue())
        .isTrue();
  }

  @Test
  public void javlue_returns_java_false_from_false_bool() {
    Bool bool = boolV(false);
    assertThat(bool.jValue())
        .isFalse();
  }

  @Test
  public void bools_with_equal_values_are_equal() {
    assertThat(boolV(true))
        .isEqualTo(boolV(true));
  }

  @Test
  public void bools_with_different_values_are_not_equal() {
    assertThat(boolV(true))
        .isNotEqualTo(boolV(false));
  }

  @Test
  public void hash_of_true_bools_are_the_same() {
    assertThat(boolV(true).hash())
        .isEqualTo(boolV(true).hash());
  }

  @Test
  public void hash_of_false_bools_are_the_same() {
    assertThat(boolV(false).hash())
        .isEqualTo(boolV(false).hash());
  }

  @Test
  public void hash_of_bools_with_different_values_is_not_the_same() {
    assertThat(boolV(true).hash())
        .isNotEqualTo(boolV(false).hash());
  }

  @Test
  public void hash_code_of_true_bools_is_the_same() {
    assertThat(boolV(true).hashCode())
        .isEqualTo(boolV(true).hashCode());
  }

  @Test
  public void hash_code_of_false_bools_is_the_same() {
    assertThat(boolV(false).hashCode())
        .isEqualTo(boolV(false).hashCode());
  }

  @Test
  public void hash_code_of_bools_with_different_values_is_not_the_same() {
    assertThat(boolV(true).hashCode())
        .isNotEqualTo(boolV(false).hashCode());
  }

  @Test
  public void bool_can_be_read_back_by_hash() {
    Bool bool = boolV(true);
    Hash hash = bool.hash();
    assertThat(objectDbOther().get(hash))
        .isEqualTo(bool);
  }

  @Test
  public void bool_read_back_by_hash_has_same_jvalue() {
    Bool bool = boolV(true);
    assertThat(((Bool) objectDbOther().get(bool.hash())).jValue())
        .isTrue();
  }

  @Test
  public void to_string_contains_value() {
    Bool bool = boolV(true);
    assertThat(bool.toString())
        .isEqualTo("true:" + bool.hash());
  }
}
