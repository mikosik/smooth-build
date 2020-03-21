package org.smoothbuild.lang.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.smoothbuild.testing.TestingContext;

public class StringTest extends TestingContext {
  private final String string = "my string";
  private final String otherString = "my string 2";

  @Test
  public void type_of_sstring_is_sstring() {
    assertThat(string(string).type())
        .isEqualTo(stringType());
  }

  @Test
  public void jvalue_returns_java_string() {
    assertThat(string(string).jValue())
        .isEqualTo(string);
  }

  @Test
  public void jvalue_returns_empty_java_string_for_empty_sstring() {
    assertThat(string("").jValue())
        .isEqualTo("");
  }

  @Test
  public void sstrings_with_equal_values_are_equal() {
    assertThat(string(string))
        .isEqualTo(string(string));
  }

  @Test
  public void sstrings_with_different_values_are_not_equal() {
    assertThat(string(string))
        .isNotEqualTo(string(otherString));
  }

  @Test
  public void hash_of_sstrings_with_equal_values_is_the_same() {
    assertThat(string(string).hash())
        .isEqualTo(string(string).hash());
  }

  @Test
  public void hash_of_sstrings_with_different_values_is_not_the_same() {
    assertThat(string(string).hash())
        .isNotEqualTo(string(otherString).hash());
  }

  @Test
  public void hash_code_of_sstrings_with_equal_values_is_the_same() {
    assertThat(string(string).hashCode())
        .isEqualTo(string(string).hashCode());
  }

  @Test
  public void hash_code_of_sstrings_with_different_values_is_not_the_same() {
    assertThat(string(string).hashCode())
        .isNotEqualTo(string(otherString).hashCode());
  }

  @Test
  public void sstring_can_be_read_back_by_hash() {
    SString sstring = string(string);
    assertThat(objectDbOther().get(sstring.hash()))
        .isEqualTo(sstring);
  }

  @Test
  public void sstring_read_back_by_hash_has_same_javlue() {
    SString sstring = string(string);
    assertThat(((SString) objectDbOther().get(sstring.hash())).jValue())
        .isEqualTo(string);
  }

  @Test
  public void to_string_contains_string_value() {
    SString sstring = string(string);
    assertThat(sstring.toString())
        .isEqualTo("String(\"my string\"):" + sstring.hash());
  }

  @Test
  public void to_string_contains_shortened_string_value_for_long_strings() {
    SString sstring = string("123456789012345678901234567890");
    assertThat(sstring.toString())
        .isEqualTo("String(\"1234567890123456789012345\"...):" + sstring.hash());
  }

  @Test
  public void to_string_contains_properly_escaped_special_characters() {
    SString sstring = string("\t \b \n \r \f \" \\");
    assertThat(sstring.toString())
        .isEqualTo("String(\"\\t \\b \\n \\r \\f \\\" \\\\\"):" + sstring.hash());
  }
}
