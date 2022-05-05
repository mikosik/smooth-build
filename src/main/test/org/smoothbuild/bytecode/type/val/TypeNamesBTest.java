package org.smoothbuild.bytecode.type.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.bytecode.type.val.TypeNamesB.arrayTypeName;
import static org.smoothbuild.bytecode.type.val.TypeNamesB.funcTypeName;
import static org.smoothbuild.bytecode.type.val.TypeNamesB.isVarName;
import static org.smoothbuild.bytecode.type.val.VarSetB.varSetB;
import static org.smoothbuild.testing.type.TestingTB.BLOB;
import static org.smoothbuild.testing.type.TestingTB.BOOL;
import static org.smoothbuild.testing.type.TestingTB.STRING;
import static org.smoothbuild.testing.type.TestingTB.VAR_A;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TypeNamesBTest {
  @Nested
  class _var_name {
    @Test
    public void empty_string_is_not_type_var_name() {
      assertThat(isVarName(""))
          .isFalse();
    }

    @Test
    public void lowercase_a_character_is_not_type_var_name() {
      assertThat(isVarName("a"))
          .isFalse();
    }

    @Test
    public void lowercase_b_character_is_not_type_var_name() {
      assertThat(isVarName("b"))
          .isFalse();
    }

    @Test
    public void uppercase_a_character_is_type_var_name() {
      assertThat(isVarName("A"))
          .isTrue();
    }

    @Test
    public void uppercase_b_character_is_type_var_name() {
      assertThat(isVarName("B"))
          .isTrue();
    }

    @Test
    public void longer_string_starting_with_lowercase_is_not_type_var_name() {
      assertThat(isVarName("alphabet"))
          .isFalse();
    }

    @Test
    public void longer_string_starting_with_uppercase_is_not_type_var_name() {
      assertThat(isVarName("Alphabet"))
          .isFalse();
    }
  }

  @Nested
  class _array_type_name {
    @Test
    public void array_type_name() {
      assertThat(arrayTypeName(STRING))
          .isEqualTo("[String]");
    }
  }

  @Nested
  class _func_type_name {
    @Test
    public void func_type_name() {
      assertThat(funcTypeName(varSetB(VAR_A), STRING, list(BLOB, BOOL)))
          .isEqualTo("<A>String(Blob,Bool)");
    }
  }

  @Nested
  class _tuple_type_name {
    @Test
    public void func_type_name() {
      assertThat(TypeNamesB.tupleTypeName(list(BLOB, BOOL)))
          .isEqualTo("{Blob,Bool}");
    }
  }
}
