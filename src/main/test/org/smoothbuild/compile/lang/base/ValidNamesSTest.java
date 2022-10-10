package org.smoothbuild.compile.lang.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class ValidNamesSTest extends TestContext {
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
      assertThat(ValidNamesS.funcTypeName(stringTS(), tupleTS(blobTS(), boolTS())))
          .isEqualTo("String(Blob,Bool)");
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
