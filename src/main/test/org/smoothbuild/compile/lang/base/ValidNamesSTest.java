package org.smoothbuild.compile.lang.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class ValidNamesSTest extends TestContext {
  @Test
  public void is_upper_case() {
    for (char i = 'A'; i <= 'Z'; i++) {
      assertThat(ValidNamesS.isUpperCase(i))
          .isTrue();
    }
    for (char i = 'a'; i <= 'z'; i++) {
      assertThat(ValidNamesS.isUpperCase(i))
          .isFalse();
    }
    for (char i = '0'; i <= '9'; i++) {
      assertThat(ValidNamesS.isUpperCase(i))
          .isFalse();
    }
    assertThat(ValidNamesS.isUpperCase('_'))
        .isFalse();
  }

  @Test
  public void is_lower_case() {
    for (char i = 'A'; i <= 'Z'; i++) {
      assertThat(ValidNamesS.isLowerCase(i))
          .isFalse();
    }
    for (char i = 'a'; i <= 'z'; i++) {
      assertThat(ValidNamesS.isLowerCase(i))
          .isTrue();
    }
    for (char i = '0'; i <= '9'; i++) {
      assertThat(ValidNamesS.isLowerCase(i))
          .isFalse();
    }
    assertThat(ValidNamesS.isLowerCase('_'))
        .isFalse();
  }

  @Nested
  class _starts_with_upper_case {
    @Test
    public void lower_case() {
      assertThat(ValidNamesS.startsWithUpperCase("abc"))
          .isFalse();
    }

    @Test
    public void upper_case() {
      assertThat(ValidNamesS.startsWithUpperCase("Abc"))
          .isTrue();
    }

    @Test
    public void empty_string_does_not_start_with_uppercase() {
      assertThat(ValidNamesS.startsWithUpperCase(""))
          .isFalse();
    }

    @Test
    public void digit() {
      assertThat(ValidNamesS.startsWithUpperCase("3"))
          .isFalse();
    }

    @Test
    public void underscore() {
      assertThat(ValidNamesS.startsWithLowerCase("_"))
          .isFalse();
    }
  }

  @Nested
  class _starts_with_lower_case {
    @Test
    public void lower_case() {
      assertThat(ValidNamesS.startsWithLowerCase("abc"))
          .isTrue();
    }

    @Test
    public void upper_case() {
      assertThat(ValidNamesS.startsWithLowerCase("Abc"))
          .isFalse();
    }

    @Test
    public void empty_string_does_not_start_with_lowercase() {
      assertThat(ValidNamesS.startsWithLowerCase(""))
          .isFalse();
    }

    @Test
    public void digit() {
      assertThat(ValidNamesS.startsWithLowerCase("3"))
          .isFalse();
    }

    @Test
    public void underscore() {
      assertThat(ValidNamesS.startsWithLowerCase("_"))
          .isFalse();
    }
  }

  @Nested
  class _var_name {
    @Test
    public void uppercase_A_character_is_type_var_name() {
      assertThat(ValidNamesS.isVarName("A"))
          .isTrue();
    }

    @Test
    public void lowercase_a_character_is_not_type_var_name() {
      assertThat(ValidNamesS.isVarName("a"))
          .isFalse();
    }

    @Test
    public void uppercase_AA_character_is_type_var_name() {
      assertThat(ValidNamesS.isVarName("AA"))
          .isTrue();
    }

    @Test
    public void mixed_case_Aa_character_is_not_type_var_name() {
      assertThat(ValidNamesS.isVarName("Aa"))
          .isFalse();
    }

    @Test
    public void uppercase_B_character_is_type_var_name() {
      assertThat(ValidNamesS.isVarName("B"))
          .isTrue();
    }

    @Test
    public void lowercase_b_character_is_not_type_var_name() {
      assertThat(ValidNamesS.isVarName("b"))
          .isFalse();
    }

    @Test
    public void empty_string_is_not_type_var_name() {
      assertThat(ValidNamesS.isVarName(""))
          .isFalse();
    }

    @Test
    public void underscore_character_is_not_type_var_name() {
      assertThat(ValidNamesS.isVarName("_"))
          .isFalse();
    }

    @Test
    public void uppercase_A_with_underscore_character_is_not_type_var_name() {
      assertThat(ValidNamesS.isVarName("A_"))
          .isFalse();
    }

    @Test
    public void whitespace_string_is_not_type_var_name() {
      assertThat(ValidNamesS.isVarName(" "))
          .isFalse();
    }

    @Test
    public void longer_string_starting_with_lowercase_is_not_type_var_name() {
      assertThat(ValidNamesS.isVarName("alphabet"))
          .isFalse();
    }

    @Test
    public void longer_string_starting_with_uppercase_is_not_type_var_name() {
      assertThat(ValidNamesS.isVarName("Alphabet"))
          .isFalse();
    }
  }

  @Nested
  class _array_type_name {
    @Test
    public void array_type_name() {
      assertThat(ValidNamesS.arrayTypeName(stringTS()))
          .isEqualTo("[String]");
    }
  }

  @Nested
  class _func_type_name {
    @Test
    public void func_type_name() {
      assertThat(ValidNamesS.funcTypeName(tupleTS(blobTS(), boolTS()), stringTS()))
          .isEqualTo("(Blob,Bool)->String");
    }
  }

  @Nested
  class _struct_name_to_ctor_name {
    @Test
    public void func_type_name() {
      assertThat(ValidNamesS.structNameToCtorName("MyStruct"))
          .isEqualTo("myStruct");
    }
  }
}
