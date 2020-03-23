package org.smoothbuild.lang.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.object.type.TypeNames.isGenericTypeName;

import org.junit.jupiter.api.Test;

public class TypeNamesTest {
  @Test
  public void empty_string_is_not_generic_type_name() {
    assertThat(isGenericTypeName(""))
        .isFalse();
  }

  @Test
  public void lowercase_a_character_is_not_generic_type_name() {
    assertThat(isGenericTypeName("a"))
        .isFalse();
  }

  @Test
  public void lowercase_b_character_is_not_generic_type_name() {
    assertThat(isGenericTypeName("b"))
        .isFalse();
  }

  @Test
  public void uppercase_a_character_is_generic_type_name() {
    assertThat(isGenericTypeName("A"))
        .isTrue();
  }

  @Test
  public void uppercase_b_character_is_generic_type_name() {
    assertThat(isGenericTypeName("B"))
        .isTrue();
  }

  @Test
  public void longer_string_starting_with_lowercase_is_not_generic_type_name() {
    assertThat(isGenericTypeName("alphabet"))
        .isFalse();
  }

  @Test
  public void longer_string_starting_with_uppercase_is_not_generic_type_name() {
    assertThat(isGenericTypeName("Alphabet"))
        .isFalse();
  }
}
