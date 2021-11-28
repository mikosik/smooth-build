package org.smoothbuild.lang.base.type.api;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypesS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypesS.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.lang.base.type.api.TypeNames.arrayTypeName;
import static org.smoothbuild.lang.base.type.api.TypeNames.functionTypeName;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TypeNamesTest {
  @Nested
  class _variable_name {
    @Test
    public void empty_string_is_not_type_variable_name() {
      assertThat(isVariableName(""))
          .isFalse();
    }

    @Test
    public void lowercase_a_character_is_not_type_variable_name() {
      assertThat(isVariableName("a"))
          .isFalse();
    }

    @Test
    public void lowercase_b_character_is_not_type_variable_name() {
      assertThat(isVariableName("b"))
          .isFalse();
    }

    @Test
    public void uppercase_a_character_is_type_variable_name() {
      assertThat(isVariableName("A"))
          .isTrue();
    }

    @Test
    public void uppercase_b_character_is_type_variable_name() {
      assertThat(isVariableName("B"))
          .isTrue();
    }

    @Test
    public void longer_string_starting_with_lowercase_is_not_type_variable_name() {
      assertThat(isVariableName("alphabet"))
          .isFalse();
    }

    @Test
    public void longer_string_starting_with_uppercase_is_not_type_variable_name() {
      assertThat(isVariableName("Alphabet"))
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
  class _function_type_name {
    @Test
    public void function_type_name() {
      assertThat(functionTypeName(STRING, list(BLOB, BOOL)))
          .isEqualTo("String(Blob, Bool)");
    }
  }
}
