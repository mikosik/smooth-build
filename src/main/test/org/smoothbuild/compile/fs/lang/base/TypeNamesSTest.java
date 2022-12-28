package org.smoothbuild.compile.fs.lang.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class TypeNamesSTest extends TestContext {
  @Test
  public void is_upper_case() {
    for (char i = 'A'; i <= 'Z'; i++) {
      assertThat(TypeNamesS.isUpperCase(i))
          .isTrue();
    }
    for (char i = 'a'; i <= 'z'; i++) {
      assertThat(TypeNamesS.isUpperCase(i))
          .isFalse();
    }
    for (char i = '0'; i <= '9'; i++) {
      assertThat(TypeNamesS.isUpperCase(i))
          .isFalse();
    }
    assertThat(TypeNamesS.isUpperCase('_'))
        .isFalse();
  }

  @Test
  public void is_lower_case() {
    for (char i = 'A'; i <= 'Z'; i++) {
      assertThat(TypeNamesS.isLowerCase(i))
          .isFalse();
    }
    for (char i = 'a'; i <= 'z'; i++) {
      assertThat(TypeNamesS.isLowerCase(i))
          .isTrue();
    }
    for (char i = '0'; i <= '9'; i++) {
      assertThat(TypeNamesS.isLowerCase(i))
          .isFalse();
    }
    assertThat(TypeNamesS.isLowerCase('_'))
        .isFalse();
  }

  @Nested
  class _starts_with_upper_case {
    @Test
    public void lower_case() {
      assertThat(TypeNamesS.startsWithUpperCase("abc"))
          .isFalse();
    }

    @Test
    public void upper_case() {
      assertThat(TypeNamesS.startsWithUpperCase("Abc"))
          .isTrue();
    }

    @Test
    public void empty_string_does_not_start_with_uppercase() {
      assertThat(TypeNamesS.startsWithUpperCase(""))
          .isFalse();
    }

    @Test
    public void digit() {
      assertThat(TypeNamesS.startsWithUpperCase("3"))
          .isFalse();
    }

    @Test
    public void underscore() {
      assertThat(TypeNamesS.startsWithLowerCase("_"))
          .isFalse();
    }
  }

  @Nested
  class _starts_with_lower_case {
    @Test
    public void lower_case() {
      assertThat(TypeNamesS.startsWithLowerCase("abc"))
          .isTrue();
    }

    @Test
    public void upper_case() {
      assertThat(TypeNamesS.startsWithLowerCase("Abc"))
          .isFalse();
    }

    @Test
    public void empty_string_does_not_start_with_lowercase() {
      assertThat(TypeNamesS.startsWithLowerCase(""))
          .isFalse();
    }

    @Test
    public void digit() {
      assertThat(TypeNamesS.startsWithLowerCase("3"))
          .isFalse();
    }

    @Test
    public void underscore() {
      assertThat(TypeNamesS.startsWithLowerCase("_"))
          .isFalse();
    }
  }

  @Nested
  class _var_name {
    @Test
    public void uppercase_A_character_is_type_var_name() {
      assertThat(TypeNamesS.isVarName("A"))
          .isTrue();
    }

    @Test
    public void lowercase_a_character_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName("a"))
          .isFalse();
    }

    @Test
    public void uppercase_AA_character_is_type_var_name() {
      assertThat(TypeNamesS.isVarName("AA"))
          .isTrue();
    }

    @Test
    public void mixed_case_Aa_character_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName("Aa"))
          .isFalse();
    }

    @Test
    public void uppercase_B_character_is_type_var_name() {
      assertThat(TypeNamesS.isVarName("B"))
          .isTrue();
    }

    @Test
    public void lowercase_b_character_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName("b"))
          .isFalse();
    }

    @Test
    public void empty_string_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName(""))
          .isFalse();
    }

    @Test
    public void underscore_character_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName("_"))
          .isFalse();
    }

    @Test
    public void uppercase_A_with_underscore_character_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName("A_"))
          .isFalse();
    }

    @Test
    public void whitespace_string_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName(" "))
          .isFalse();
    }

    @Test
    public void longer_string_starting_with_lowercase_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName("alphabet"))
          .isFalse();
    }

    @Test
    public void longer_string_starting_with_uppercase_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName("Alphabet"))
          .isFalse();
    }
  }

  @Nested
  class _array_type_name {
    @Test
    public void array_type_name() {
      assertThat(TypeNamesS.arrayTypeName(stringTS()))
          .isEqualTo("[String]");
    }
  }

  @Nested
  class _func_type_name {
    @Test
    public void func_type_name() {
      assertThat(TypeNamesS.funcTypeName(tupleTS(blobTS(), boolTS()), stringTS()))
          .isEqualTo("(Blob,Bool)->String");
    }
  }
}
