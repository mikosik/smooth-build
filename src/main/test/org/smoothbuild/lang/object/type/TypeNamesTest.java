package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeNames.isGenericTypeName;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class TypeNamesTest {
  @Test
  public void empty_string_is_not_generic_type_name() {
    when(() -> isGenericTypeName(""));
    thenReturned(false);
  }

  @Test
  public void lowercase_a_character_is_not_generic_type_name() {
    when(() -> isGenericTypeName("a"));
    thenReturned(false);
  }

  @Test
  public void lowercase_b_character_is_not_generic_type_name() {
    when(() -> isGenericTypeName("b"));
    thenReturned(false);
  }

  @Test
  public void uppercase_a_character_is_generic_type_name() {
    when(() -> isGenericTypeName("A"));
    thenReturned(true);
  }

  @Test
  public void uppercase_b_character_is_generic_type_name() {
    when(() -> isGenericTypeName("B"));
    thenReturned(true);
  }

  @Test
  public void longer_string_starting_with_lowercase_is_not_generic_type_name() {
    when(() -> isGenericTypeName("alphabet"));
    thenReturned(false);
  }

  @Test
  public void longer_string_starting_with_uppercase_is_not_generic_type_name() {
    when(() -> isGenericTypeName("Alphabet"));
    thenReturned(false);
  }
}
