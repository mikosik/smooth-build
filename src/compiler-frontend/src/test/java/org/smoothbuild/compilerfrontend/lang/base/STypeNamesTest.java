package org.smoothbuild.compilerfrontend.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.base.STypeNames.arrayTypeName;
import static org.smoothbuild.compilerfrontend.lang.base.STypeNames.funcTypeName;
import static org.smoothbuild.compilerfrontend.lang.base.STypeNames.isLowerCase;
import static org.smoothbuild.compilerfrontend.lang.base.STypeNames.isUpperCase;
import static org.smoothbuild.compilerfrontend.lang.base.STypeNames.isVarName;
import static org.smoothbuild.compilerfrontend.lang.base.STypeNames.startsWithLowerCase;
import static org.smoothbuild.compilerfrontend.lang.base.STypeNames.startsWithUpperCase;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class STypeNamesTest extends FrontendCompilerTestContext {
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

  @Nested
  class _starts_with_upper_case {
    @Test
    void lower_case() {
      assertThat(startsWithUpperCase("abc")).isFalse();
    }

    @Test
    void upper_case() {
      assertThat(startsWithUpperCase("Abc")).isTrue();
    }

    @Test
    void empty_string_does_not_start_with_uppercase() {
      assertThat(startsWithUpperCase("")).isFalse();
    }

    @Test
    void digit() {
      assertThat(startsWithUpperCase("3")).isFalse();
    }

    @Test
    void underscore() {
      assertThat(startsWithLowerCase("_")).isFalse();
    }
  }

  @Nested
  class _starts_with_lower_case {
    @Test
    void lower_case() {
      assertThat(startsWithLowerCase("abc")).isTrue();
    }

    @Test
    void upper_case() {
      assertThat(startsWithLowerCase("Abc")).isFalse();
    }

    @Test
    void empty_string_does_not_start_with_lowercase() {
      assertThat(startsWithLowerCase("")).isFalse();
    }

    @Test
    void digit() {
      assertThat(startsWithLowerCase("3")).isFalse();
    }

    @Test
    void underscore() {
      assertThat(startsWithLowerCase("_")).isFalse();
    }
  }

  @Nested
  class _var_name {
    @Test
    void uppercase_A_character_is_type_var_name() {
      assertThat(isVarName("A")).isTrue();
    }

    @Test
    void lowercase_a_character_is_not_type_var_name() {
      assertThat(isVarName("a")).isFalse();
    }

    @Test
    void uppercase_AA_character_is_type_var_name() {
      assertThat(isVarName("AA")).isTrue();
    }

    @Test
    void mixed_case_Aa_character_is_not_type_var_name() {
      assertThat(isVarName("Aa")).isFalse();
    }

    @Test
    void uppercase_B_character_is_type_var_name() {
      assertThat(isVarName("B")).isTrue();
    }

    @Test
    void lowercase_b_character_is_not_type_var_name() {
      assertThat(isVarName("b")).isFalse();
    }

    @Test
    void empty_string_is_not_type_var_name() {
      assertThat(isVarName("")).isFalse();
    }

    @Test
    void underscore_character_is_not_type_var_name() {
      assertThat(isVarName("_")).isFalse();
    }

    @Test
    void uppercase_A_with_underscore_character_is_not_type_var_name() {
      assertThat(isVarName("A_")).isFalse();
    }

    @Test
    void whitespace_string_is_not_type_var_name() {
      assertThat(isVarName(" ")).isFalse();
    }

    @Test
    void longer_string_starting_with_lowercase_is_not_type_var_name() {
      assertThat(isVarName("alphabet")).isFalse();
    }

    @Test
    void longer_string_starting_with_uppercase_is_not_type_var_name() {
      assertThat(isVarName("Alphabet")).isFalse();
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
