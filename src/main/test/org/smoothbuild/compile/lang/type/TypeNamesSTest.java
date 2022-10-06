package org.smoothbuild.compile.lang.type;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class TypeNamesSTest extends TestContext {
  @Nested
  class _var_name {
    @Test
    public void empty_string_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName(""))
          .isFalse();
    }

    @Test
    public void lowercase_a_character_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName("a"))
          .isFalse();
    }

    @Test
    public void lowercase_b_character_is_not_type_var_name() {
      assertThat(TypeNamesS.isVarName("b"))
          .isFalse();
    }

    @Test
    public void uppercase_a_character_is_type_var_name() {
      assertThat(TypeNamesS.isVarName("A"))
          .isTrue();
    }

    @Test
    public void uppercase_b_character_is_type_var_name() {
      assertThat(TypeNamesS.isVarName("B"))
          .isTrue();
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
      assertThat(TypeNamesS.funcTypeName(stringTS(), tupleTS(blobTS(), boolTS())))
          .isEqualTo("String(Blob,Bool)");
    }
  }

  @Nested
  class _struct_name_to_ctor_name {
    @Test
    public void func_type_name() {
      assertThat(TypeNamesS.structNameToCtorName("MyStruct"))
          .isEqualTo("myStruct");
    }
  }
}
