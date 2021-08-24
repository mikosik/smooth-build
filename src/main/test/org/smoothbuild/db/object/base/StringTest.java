package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class StringTest extends TestingContext {
  private final String string = "my string";
  private final String otherString = "my string 2";

  @Test
  public void spec_of_str_is_str() {
    assertThat(strV(string).spec())
        .isEqualTo(strS());
  }

  @Test
  public void jvalue_returns_java_string() {
    assertThat(strV(string).jValue())
        .isEqualTo(string);
  }

  @Test
  public void jvalue_returns_empty_java_string_for_empty_str() {
    assertThat(strV("").jValue())
        .isEqualTo("");
  }

  @Test
  public void strs_with_equal_values_are_equal() {
    assertThat(strV(string))
        .isEqualTo(strV(string));
  }

  @Test
  public void strs_with_different_values_are_not_equal() {
    assertThat(strV(string))
        .isNotEqualTo(strV(otherString));
  }

  @Test
  public void hash_of_strs_with_equal_values_is_the_same() {
    assertThat(strV(string).hash())
        .isEqualTo(strV(string).hash());
  }

  @Test
  public void hash_of_strs_with_different_values_is_not_the_same() {
    assertThat(strV(string).hash())
        .isNotEqualTo(strV(otherString).hash());
  }

  @Test
  public void hash_code_of_strs_with_equal_values_is_the_same() {
    assertThat(strV(string).hashCode())
        .isEqualTo(strV(string).hashCode());
  }

  @Test
  public void hash_code_of_strs_with_different_values_is_not_the_same() {
    assertThat(strV(string).hashCode())
        .isNotEqualTo(strV(otherString).hashCode());
  }

  @Test
  public void str_can_be_read_back_by_hash() {
    Str str = strV(string);
    assertThat(objectDbOther().get(str.hash()))
        .isEqualTo(str);
  }

  @Test
  public void str_read_back_by_hash_has_same_jvalue() {
    Str str = strV(string);
    assertThat(((Str) objectDbOther().get(str.hash())).jValue())
        .isEqualTo(string);
  }

  @Test
  public void to_string_contains_string_value() {
    Str str = strV(string);
    assertThat(str.toString())
        .isEqualTo("""
            "my string":""" + str.hash());
  }

  @Test
  public void to_string_contains_shortened_string_value_for_long_strings() {
    Str str = strV("123456789012345678901234567890");
    assertThat(str.toString())
        .isEqualTo("""
            "1234567890123456789012345"...:""" + str.hash());
  }

  @Test
  public void to_string_contains_properly_escaped_special_characters() {
    Str str = strV("\t \b \n \r \f \" \\");
    assertThat(str.toString())
        .isEqualTo("""
            "\\t \\b \\n \\r \\f \\" \\\\":""" + str.hash());
  }
}
