package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.Types.isTypeVariableName;

import org.junit.jupiter.api.Test;

public class TypesTest {
  @Test
  public void empty_string_is_not_type_variable_name() {
    assertThat(isTypeVariableName(""))
        .isFalse();
  }

  @Test
  public void lowercase_a_character_is_not_type_variable_name() {
    assertThat(isTypeVariableName("a"))
        .isFalse();
  }

  @Test
  public void lowercase_b_character_is_not_type_variable_name() {
    assertThat(isTypeVariableName("b"))
        .isFalse();
  }

  @Test
  public void uppercase_a_character_is_type_variable_name() {
    assertThat(isTypeVariableName("A"))
        .isTrue();
  }

  @Test
  public void uppercase_b_character_is_type_variable_name() {
    assertThat(isTypeVariableName("B"))
        .isTrue();
  }

  @Test
  public void longer_string_starting_with_lowercase_is_not_type_variable_name() {
    assertThat(isTypeVariableName("alphabet"))
        .isFalse();
  }

  @Test
  public void longer_string_starting_with_uppercase_is_not_type_variable_name() {
    assertThat(isTypeVariableName("Alphabet"))
        .isFalse();
  }
}
