package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.api.ItemSignature.itemSignature;
import static org.smoothbuild.lang.base.type.api.TypeNames.arrayTypeName;
import static org.smoothbuild.lang.base.type.api.TypeNames.functionTypeName;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.api.ItemSignature;
import org.smoothbuild.lang.base.type.impl.BaseTypeImpl;

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
      assertThat(arrayTypeName(type("MyType")))
          .isEqualTo("[MyType]");
    }
  }

  @Nested
  class _function_type_name {
    @Test
    public void function_type_name() {
      assertThat(functionTypeName(type("ResultType"), list(itemSig("Type1"), itemSig("Type2"))))
          .isEqualTo("ResultType(Type1, Type2)");
    }
  }

  private ItemSignature itemSig(String name) {
    return itemSignature(type(name));
  }

  private BaseTypeImpl type(String resultType) {
    return new BaseTypeImpl(resultType);
  }
}
