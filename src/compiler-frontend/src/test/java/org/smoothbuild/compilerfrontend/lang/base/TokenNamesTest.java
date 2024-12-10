package org.smoothbuild.compilerfrontend.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.arrayTypeName;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.detectIdentifierNameErrors;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.detectStructNameErrors;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.funcTypeName;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.isLowerCase;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.isTypeVarName;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.isUpperCase;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class TokenNamesTest extends FrontendCompilerTestContext {
  @Test
  void is_upper_case() {
    for (char i = 'A'; i <= 'Z'; i++) {
      assertThat(isUpperCase(i)).isTrue();
    }
    for (char i = 'a'; i <= 'z'; i++) {
      assertThat(isUpperCase(i)).isFalse();
    }
    for (char i = '0'; i <= '9'; i++) {
      assertThat(isUpperCase(i)).isFalse();
    }
    assertThat(isUpperCase('_')).isFalse();
  }

  @Test
  void is_lower_case() {
    for (char i = 'A'; i <= 'Z'; i++) {
      assertThat(isLowerCase(i)).isFalse();
    }
    for (char i = 'a'; i <= 'z'; i++) {
      assertThat(isLowerCase(i)).isTrue();
    }
    for (char i = '0'; i <= '9'; i++) {
      assertThat(isLowerCase(i)).isFalse();
    }
    assertThat(isLowerCase('_')).isFalse();
  }

  @ParameterizedTest
  @MethodSource
  void detect_struct_name_errors(String name, Maybe<String> expected) {
    assertThat(detectStructNameErrors(name)).isEqualTo(expected);
  }

  static List<Arguments> detect_struct_name_errors() {
    return list(
        arguments("Abc", none()),
        arguments("A3", none()),
        arguments("abc", some("It must start with uppercase letter.")),
        arguments("aBC", some("It must start with uppercase letter.")),
        arguments("ABC", some("All-uppercase names are reserved for type variables.")),
        arguments("", some("It must not be empty string.")),
        arguments("_", some("`_` is reserved for future use.")),
        arguments("3", some("It must start with uppercase letter.")),
        arguments("My:Struct", some("It must not contain ':' character.")),
        arguments("My^Struct", some("It must not contain '^' character.")));
  }

  @ParameterizedTest
  @MethodSource
  void detect_identifier_name_errors(String name, Maybe<String> expected) {
    assertThat(detectIdentifierNameErrors(name)).isEqualTo(expected);
  }

  static List<Arguments> detect_identifier_name_errors() {
    return list(
        arguments("abc", none()),
        arguments("aBC", none()),
        arguments("a3", none()),
        arguments("Abc", some("It must start with lowercase letter.")),
        arguments("ABC", some("It must start with lowercase letter.")),
        arguments("", some("It must not be empty string.")),
        arguments("_", some("`_` is reserved for future use.")),
        arguments("3", some("It must start with lowercase letter.")),
        arguments("my:identifier", some("It must not contain ':' character.")),
        arguments("my^identifier", some("It must not contain '^' character.")));
  }

  @Nested
  class _type_variable_name {
    @Test
    void uppercase_A_character_is_type_var_name() {
      assertThat(isTypeVarName("A")).isTrue();
    }

    @Test
    void lowercase_a_character_is_not_type_var_name() {
      assertThat(isTypeVarName("a")).isFalse();
    }

    @Test
    void uppercase_AA_character_is_type_var_name() {
      assertThat(isTypeVarName("AA")).isTrue();
    }

    @Test
    void mixed_case_Aa_character_is_not_type_var_name() {
      assertThat(isTypeVarName("Aa")).isFalse();
    }

    @Test
    void uppercase_B_character_is_type_var_name() {
      assertThat(isTypeVarName("B")).isTrue();
    }

    @Test
    void lowercase_b_character_is_not_type_var_name() {
      assertThat(isTypeVarName("b")).isFalse();
    }

    @Test
    void empty_string_is_not_type_var_name() {
      assertThat(isTypeVarName("")).isFalse();
    }

    @Test
    void underscore_character_is_not_type_var_name() {
      assertThat(isTypeVarName("_")).isFalse();
    }

    @Test
    void uppercase_A_with_underscore_character_is_not_type_var_name() {
      assertThat(isTypeVarName("A_")).isFalse();
    }

    @Test
    void whitespace_string_is_not_type_var_name() {
      assertThat(isTypeVarName(" ")).isFalse();
    }

    @Test
    void longer_string_starting_with_lowercase_is_not_type_var_name() {
      assertThat(isTypeVarName("alphabet")).isFalse();
    }

    @Test
    void longer_string_starting_with_uppercase_is_not_type_var_name() {
      assertThat(isTypeVarName("Alphabet")).isFalse();
    }
  }

  @Nested
  class _array_type_name {
    @Test
    void array_type_name() {
      assertThat(arrayTypeName(sStringType())).isEqualTo("[String]");
    }
  }

  @Nested
  class _func_type_name {
    @Test
    void func_type_name() {
      assertThat(funcTypeName(sTupleType(sBlobType(), sBoolType()), sStringType()))
          .isEqualTo("(Blob,Bool)->String");
    }
  }
}
