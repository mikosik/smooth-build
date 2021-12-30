package org.smoothbuild.bytecode.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.testing.TestingContext;

public class StringBTest extends TestingContext {
  private final String string = "my string";
  private final String otherString = "my string 2";

  @Test
  public void type_of_string_is_string_type() {
    assertThat(stringB(string).cat())
        .isEqualTo(stringTB());
  }

  @Test
  public void to_j_returns_java_string() {
    assertThat(stringB(string).toJ())
        .isEqualTo(string);
  }

  @Test
  public void to_j_returns_empty_java_string_for_empty_str() {
    assertThat(stringB("").toJ())
        .isEqualTo("");
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<StringB> {
    @Override
    protected List<StringB> equalValues() {
      return list(
          stringB("abc"),
          stringB("abc")
      );
    }

    @Override
    protected List<StringB> nonEqualValues() {
      return list(
          stringB(""),
          stringB("abc"),
          stringB("ABC"),
          stringB(" abc"),
          stringB("abc ")
      );
    }
  }

  @Test
  public void str_can_be_read_back_by_hash() {
    StringB str = stringB(string);
    assertThat(byteDbOther().get(str.hash()))
        .isEqualTo(str);
  }

  @Test
  public void str_read_back_by_hash_has_same_to_j() {
    StringB str = stringB(string);
    assertThat(((StringB) byteDbOther().get(str.hash())).toJ())
        .isEqualTo(string);
  }

  @Test
  public void to_string_contains_string_value() {
    StringB str = stringB(string);
    assertThat(str.toString())
        .isEqualTo("""
            "my string"@""" + str.hash());
  }

  @Test
  public void to_string_contains_shortened_string_value_for_long_strings() {
    StringB str = stringB("123456789012345678901234567890");
    assertThat(str.toString())
        .isEqualTo("""
            "1234567890123456789012345"...@""" + str.hash());
  }

  @Test
  public void to_string_contains_properly_escaped_special_characters() {
    StringB str = stringB("\t \b \n \r \f \" \\");
    assertThat(str.toString())
        .isEqualTo("""
            "\\t \\b \\n \\r \\f \\" \\\\"@""" + str.hash());
  }
}
