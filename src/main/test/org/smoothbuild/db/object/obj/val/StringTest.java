package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

public class StringTest extends TestingContextImpl {
  private final String string = "my string";
  private final String otherString = "my string 2";

  @Test
  public void type_of_string_is_string_type() {
    assertThat(string(string).type())
        .isEqualTo(stringOT());
  }

  @Test
  public void jvalue_returns_java_string() {
    assertThat(string(string).jValue())
        .isEqualTo(string);
  }

  @Test
  public void jvalue_returns_empty_java_string_for_empty_str() {
    assertThat(string("").jValue())
        .isEqualTo("");
  }

  @Test
  public void strs_with_equal_values_are_equal() {
    assertThat(string(string))
        .isEqualTo(string(string));
  }

  @Test
  public void strs_with_different_values_are_not_equal() {
    assertThat(string(string))
        .isNotEqualTo(string(otherString));
  }

  @Test
  public void hash_of_strs_with_equal_values_is_the_same() {
    assertThat(string(string).hash())
        .isEqualTo(string(string).hash());
  }

  @Test
  public void hash_of_strs_with_different_values_is_not_the_same() {
    assertThat(string(string).hash())
        .isNotEqualTo(string(otherString).hash());
  }

  @Test
  public void hash_code_of_strs_with_equal_values_is_the_same() {
    assertThat(string(string).hashCode())
        .isEqualTo(string(string).hashCode());
  }

  @Test
  public void hash_code_of_strs_with_different_values_is_not_the_same() {
    assertThat(string(string).hashCode())
        .isNotEqualTo(string(otherString).hashCode());
  }

  @Test
  public void str_can_be_read_back_by_hash() {
    Str str = string(string);
    assertThat(objectDbOther().get(str.hash()))
        .isEqualTo(str);
  }

  @Test
  public void str_read_back_by_hash_has_same_jvalue() {
    Str str = string(string);
    assertThat(((Str) objectDbOther().get(str.hash())).jValue())
        .isEqualTo(string);
  }

  @Test
  public void to_string_contains_string_value() {
    Str str = string(string);
    assertThat(str.toString())
        .isEqualTo("""
            "my string"@""" + str.hash());
  }

  @Test
  public void to_string_contains_shortened_string_value_for_long_strings() {
    Str str = string("123456789012345678901234567890");
    assertThat(str.toString())
        .isEqualTo("""
            "1234567890123456789012345"...@""" + str.hash());
  }

  @Test
  public void to_string_contains_properly_escaped_special_characters() {
    Str str = string("\t \b \n \r \f \" \\");
    assertThat(str.toString())
        .isEqualTo("""
            "\\t \\b \\n \\r \\f \\" \\\\"@""" + str.hash());
  }
}
