package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class StringHTest extends TestingContext {
  private final String string = "my string";
  private final String otherString = "my string 2";

  @Test
  public void type_of_string_is_string_type() {
    assertThat(stringH(string).cat())
        .isEqualTo(stringTH());
  }

  @Test
  public void to_j_returns_java_string() {
    assertThat(stringH(string).toJ())
        .isEqualTo(string);
  }

  @Test
  public void to_j_returns_empty_java_string_for_empty_str() {
    assertThat(stringH("").toJ())
        .isEqualTo("");
  }

  @Test
  public void strs_with_equal_values_are_equal() {
    assertThat(stringH(string))
        .isEqualTo(stringH(string));
  }

  @Test
  public void strs_with_different_values_are_not_equal() {
    assertThat(stringH(string))
        .isNotEqualTo(stringH(otherString));
  }

  @Test
  public void hash_of_strs_with_equal_values_is_the_same() {
    assertThat(stringH(string).hash())
        .isEqualTo(stringH(string).hash());
  }

  @Test
  public void hash_of_strs_with_different_values_is_not_the_same() {
    assertThat(stringH(string).hash())
        .isNotEqualTo(stringH(otherString).hash());
  }

  @Test
  public void hash_code_of_strs_with_equal_values_is_the_same() {
    assertThat(stringH(string).hashCode())
        .isEqualTo(stringH(string).hashCode());
  }

  @Test
  public void hash_code_of_strs_with_different_values_is_not_the_same() {
    assertThat(stringH(string).hashCode())
        .isNotEqualTo(stringH(otherString).hashCode());
  }

  @Test
  public void str_can_be_read_back_by_hash() {
    StringH str = stringH(string);
    assertThat(objDbOther().get(str.hash()))
        .isEqualTo(str);
  }

  @Test
  public void str_read_back_by_hash_has_same_to_j() {
    StringH str = stringH(string);
    assertThat(((StringH) objDbOther().get(str.hash())).toJ())
        .isEqualTo(string);
  }

  @Test
  public void to_string_contains_string_value() {
    StringH str = stringH(string);
    assertThat(str.toString())
        .isEqualTo("""
            "my string"@""" + str.hash());
  }

  @Test
  public void to_string_contains_shortened_string_value_for_long_strings() {
    StringH str = stringH("123456789012345678901234567890");
    assertThat(str.toString())
        .isEqualTo("""
            "1234567890123456789012345"...@""" + str.hash());
  }

  @Test
  public void to_string_contains_properly_escaped_special_characters() {
    StringH str = stringH("\t \b \n \r \f \" \\");
    assertThat(str.toString())
        .isEqualTo("""
            "\\t \\b \\n \\r \\f \\" \\\\"@""" + str.hash());
  }
}
