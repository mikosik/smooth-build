package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.Types.isVariableName;

import org.junit.jupiter.api.Test;

public class TypesTest {
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
