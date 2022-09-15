package org.smoothbuild.compile.lang.type;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class TNamesSTest extends TestContext {
  @Nested
  class _var_name {
    @Test
    public void empty_string_is_not_type_var_name() {
      assertThat(TNamesS.isVarName(""))
          .isFalse();
    }

    @Test
    public void lowercase_a_character_is_not_type_var_name() {
      assertThat(TNamesS.isVarName("a"))
          .isFalse();
    }

    @Test
    public void lowercase_b_character_is_not_type_var_name() {
      assertThat(TNamesS.isVarName("b"))
          .isFalse();
    }

    @Test
    public void uppercase_a_character_is_type_var_name() {
      assertThat(TNamesS.isVarName("A"))
          .isTrue();
    }

    @Test
    public void uppercase_b_character_is_type_var_name() {
      assertThat(TNamesS.isVarName("B"))
          .isTrue();
    }

    @Test
    public void longer_string_starting_with_lowercase_is_not_type_var_name() {
      assertThat(TNamesS.isVarName("alphabet"))
          .isFalse();
    }

    @Test
    public void longer_string_starting_with_uppercase_is_not_type_var_name() {
      assertThat(TNamesS.isVarName("Alphabet"))
          .isFalse();
    }
  }

  @Nested
  class _array_type_name {
    @Test
    public void array_type_name() {
      assertThat(TNamesS.arrayTypeName(stringTS()))
          .isEqualTo("[String]");
    }
  }

  @Nested
  class _func_type_name {
    @Test
    public void func_type_name() {
      assertThat(TNamesS.funcTypeName(stringTS(), tupleTS(blobTS(), boolTS())))
          .isEqualTo("String(Blob,Bool)");
    }
  }

  @Nested
  class _struct_name_to_ctor_name {
    @Test
    public void func_type_name() {
      assertThat(TNamesS.structNameToCtorName("MyStruct"))
          .isEqualTo("myStruct");
    }
  }
}
