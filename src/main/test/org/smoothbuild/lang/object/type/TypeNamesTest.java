package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeNames.isGenericTypeName;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class TypeNamesTest {
  @Test
  public void empty_string_is_not_generic_type_name() throws Exception {
    when(() -> isGenericTypeName(""));
    thenReturned(false);
  }

  @Test
  public void lowercase_a_character_is_generic_type_name() throws Exception {
    when(() -> isGenericTypeName("a"));
    thenReturned(true);
  }

  @Test
  public void lowercase_b_character_is_generic_type_name() throws Exception {
    when(() -> isGenericTypeName("a"));
    thenReturned(true);
  }

  @Test
  public void uppercase_a_character_is_not_generic_type_name() throws Exception {
    when(() -> isGenericTypeName("A"));
    thenReturned(false);
  }

  @Test
  public void string_starting_with_lowercase_is_generic_type_name() throws Exception {
    when(() -> isGenericTypeName("alphabet"));
    thenReturned(true);
  }

  @Test
  public void string_starting_with_uppercase_is_not_generic_type_name() throws Exception {
    when(() -> isGenericTypeName("Alphabet"));
    thenReturned(false);
  }
}
