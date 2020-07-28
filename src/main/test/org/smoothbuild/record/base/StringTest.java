package org.smoothbuild.record.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class StringTest extends TestingContext {
  private final String string = "my string";
  private final String otherString = "my string 2";

  @Test
  public void spec_of_rstring_is_rstring() {
    assertThat(string(string).spec())
        .isEqualTo(stringSpec());
  }

  @Test
  public void jvalue_returns_java_string() {
    assertThat(string(string).jValue())
        .isEqualTo(string);
  }

  @Test
  public void jvalue_returns_empty_java_string_for_empty_rstring() {
    assertThat(string("").jValue())
        .isEqualTo("");
  }

  @Test
  public void rstrings_with_equal_values_are_equal() {
    assertThat(string(string))
        .isEqualTo(string(string));
  }

  @Test
  public void rstrings_with_different_values_are_not_equal() {
    assertThat(string(string))
        .isNotEqualTo(string(otherString));
  }

  @Test
  public void hash_of_rstrings_with_equal_values_is_the_same() {
    assertThat(string(string).hash())
        .isEqualTo(string(string).hash());
  }

  @Test
  public void hash_of_rstrings_with_different_values_is_not_the_same() {
    assertThat(string(string).hash())
        .isNotEqualTo(string(otherString).hash());
  }

  @Test
  public void hash_code_of_rstrings_with_equal_values_is_the_same() {
    assertThat(string(string).hashCode())
        .isEqualTo(string(string).hashCode());
  }

  @Test
  public void hash_code_of_rstrings_with_different_values_is_not_the_same() {
    assertThat(string(string).hashCode())
        .isNotEqualTo(string(otherString).hashCode());
  }

  @Test
  public void rstring_can_be_read_back_by_hash() {
    RString rstring = string(string);
    assertThat(recordDbOther().get(rstring.hash()))
        .isEqualTo(rstring);
  }

  @Test
  public void rstring_read_back_by_hash_has_same_javlue() {
    RString rstring = string(string);
    assertThat(((RString) recordDbOther().get(rstring.hash())).jValue())
        .isEqualTo(string);
  }

  @Test
  public void to_string_contains_string_value() {
    RString rstring = string(string);
    assertThat(rstring.toString())
        .isEqualTo("""
            "my string":""" + rstring.hash());
  }

  @Test
  public void to_string_contains_shortened_string_value_for_long_strings() {
    RString rstring = string("123456789012345678901234567890");
    assertThat(rstring.toString())
        .isEqualTo("""
            "1234567890123456789012345"...:""" + rstring.hash());
  }

  @Test
  public void to_string_contains_properly_escaped_special_characters() {
    RString rstring = string("\t \b \n \r \f \" \\");
    assertThat(rstring.toString())
        .isEqualTo("""
            "\\t \\b \\n \\r \\f \\" \\\\":""" + rstring.hash());
  }
}
