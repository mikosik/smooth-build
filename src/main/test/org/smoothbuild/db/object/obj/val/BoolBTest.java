package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class BoolBTest extends TestingContext {
  @Test
  public void type_of_bool_is_bool_type() {
    assertThat(boolB(true).cat())
        .isEqualTo(boolTB());
  }

  @Test
  public void to_j_returns_java_true_from_true_bool() {
    BoolB bool = boolB(true);
    assertThat(bool.toJ())
        .isTrue();
  }

  @Test
  public void javlue_returns_java_false_from_false_bool() {
    BoolB bool = boolB(false);
    assertThat(bool.toJ())
        .isFalse();
  }

  @Test
  public void bools_with_equal_values_are_equal() {
    assertThat(boolB(true))
        .isEqualTo(boolB(true));
  }

  @Test
  public void bools_with_different_values_are_not_equal() {
    assertThat(boolB(true))
        .isNotEqualTo(boolB(false));
  }

  @Test
  public void hash_of_true_bools_are_the_same() {
    assertThat(boolB(true).hash())
        .isEqualTo(boolB(true).hash());
  }

  @Test
  public void hash_of_false_bools_are_the_same() {
    assertThat(boolB(false).hash())
        .isEqualTo(boolB(false).hash());
  }

  @Test
  public void hash_of_bools_with_different_values_is_not_the_same() {
    assertThat(boolB(true).hash())
        .isNotEqualTo(boolB(false).hash());
  }

  @Test
  public void hash_code_of_true_bools_is_the_same() {
    assertThat(boolB(true).hashCode())
        .isEqualTo(boolB(true).hashCode());
  }

  @Test
  public void hash_code_of_false_bools_is_the_same() {
    assertThat(boolB(false).hashCode())
        .isEqualTo(boolB(false).hashCode());
  }

  @Test
  public void hash_code_of_bools_with_different_values_is_not_the_same() {
    assertThat(boolB(true).hashCode())
        .isNotEqualTo(boolB(false).hashCode());
  }

  @Test
  public void bool_can_be_read_back_by_hash() {
    BoolB bool = boolB(true);
    Hash hash = bool.hash();
    assertThat(byteDbOther().get(hash))
        .isEqualTo(bool);
  }

  @Test
  public void bool_read_back_by_hash_has_same_to_j() {
    BoolB bool = boolB(true);
    assertThat(((BoolB) byteDbOther().get(bool.hash())).toJ())
        .isTrue();
  }

  @Test
  public void to_string_contains_value() {
    BoolB bool = boolB(true);
    assertThat(bool.toString())
        .isEqualTo("true@" + bool.hash());
  }
}
