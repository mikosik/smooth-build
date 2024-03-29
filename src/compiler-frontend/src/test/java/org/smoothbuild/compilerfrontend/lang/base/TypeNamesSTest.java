package org.smoothbuild.compilerfrontend.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.base.TypeNamesS.arrayTypeName;
import static org.smoothbuild.compilerfrontend.lang.base.TypeNamesS.funcTypeName;
import static org.smoothbuild.compilerfrontend.lang.base.TypeNamesS.isLowerCase;
import static org.smoothbuild.compilerfrontend.lang.base.TypeNamesS.isUpperCase;
import static org.smoothbuild.compilerfrontend.lang.base.TypeNamesS.isVarName;
import static org.smoothbuild.compilerfrontend.lang.base.TypeNamesS.startsWithLowerCase;
import static org.smoothbuild.compilerfrontend.lang.base.TypeNamesS.startsWithUpperCase;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBlobType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBoolType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sTupleType;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TypeNamesSTest {
  @Test
  public void is_upper_case() {
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
  public void is_lower_case() {
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
    public void lower_case() {
      assertThat(startsWithUpperCase("abc")).isFalse();
    }

    @Test
    public void upper_case() {
      assertThat(startsWithUpperCase("Abc")).isTrue();
    }

    @Test
    public void empty_string_does_not_start_with_uppercase() {
      assertThat(startsWithUpperCase("")).isFalse();
    }

    @Test
    public void digit() {
      assertThat(startsWithUpperCase("3")).isFalse();
    }

    @Test
    public void underscore() {
      assertThat(startsWithLowerCase("_")).isFalse();
    }
  }

  @Nested
  class _starts_with_lower_case {
    @Test
    public void lower_case() {
      assertThat(startsWithLowerCase("abc")).isTrue();
    }

    @Test
    public void upper_case() {
      assertThat(startsWithLowerCase("Abc")).isFalse();
    }

    @Test
    public void empty_string_does_not_start_with_lowercase() {
      assertThat(startsWithLowerCase("")).isFalse();
    }

    @Test
    public void digit() {
      assertThat(startsWithLowerCase("3")).isFalse();
    }

    @Test
    public void underscore() {
      assertThat(startsWithLowerCase("_")).isFalse();
    }
  }

  @Nested
  class _var_name {
    @Test
    public void uppercase_A_character_is_type_var_name() {
      assertThat(isVarName("A")).isTrue();
    }

    @Test
    public void lowercase_a_character_is_not_type_var_name() {
      assertThat(isVarName("a")).isFalse();
    }

    @Test
    public void uppercase_AA_character_is_type_var_name() {
      assertThat(isVarName("AA")).isTrue();
    }

    @Test
    public void mixed_case_Aa_character_is_not_type_var_name() {
      assertThat(isVarName("Aa")).isFalse();
    }

    @Test
    public void uppercase_B_character_is_type_var_name() {
      assertThat(isVarName("B")).isTrue();
    }

    @Test
    public void lowercase_b_character_is_not_type_var_name() {
      assertThat(isVarName("b")).isFalse();
    }

    @Test
    public void empty_string_is_not_type_var_name() {
      assertThat(isVarName("")).isFalse();
    }

    @Test
    public void underscore_character_is_not_type_var_name() {
      assertThat(isVarName("_")).isFalse();
    }

    @Test
    public void uppercase_A_with_underscore_character_is_not_type_var_name() {
      assertThat(isVarName("A_")).isFalse();
    }

    @Test
    public void whitespace_string_is_not_type_var_name() {
      assertThat(isVarName(" ")).isFalse();
    }

    @Test
    public void longer_string_starting_with_lowercase_is_not_type_var_name() {
      assertThat(isVarName("alphabet")).isFalse();
    }

    @Test
    public void longer_string_starting_with_uppercase_is_not_type_var_name() {
      assertThat(isVarName("Alphabet")).isFalse();
    }
  }

  @Nested
  class _array_type_name {
    @Test
    public void array_type_name() {
      assertThat(arrayTypeName(sStringType())).isEqualTo("[String]");
    }
  }

  @Nested
  class _func_type_name {
    @Test
    public void func_type_name() {
      assertThat(funcTypeName(sTupleType(sBlobType(), sBoolType()), sStringType()))
          .isEqualTo("(Blob,Bool)->String");
    }
  }
}
