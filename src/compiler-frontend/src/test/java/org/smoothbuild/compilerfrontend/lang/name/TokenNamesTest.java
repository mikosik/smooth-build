package org.smoothbuild.compilerfrontend.lang.name;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.TokenNames.arrayTypeName;
import static org.smoothbuild.compilerfrontend.lang.name.TokenNames.funcTypeName;
import static org.smoothbuild.compilerfrontend.lang.name.TokenNames.isTypeVarName;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class TokenNamesTest extends FrontendCompilerTestContext {
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
