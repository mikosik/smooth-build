package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.testing.TestingContext;

import com.google.common.hash.HashCode;

public class SStringTest extends TestingContext {
  private SString sstring;
  private final String string = "my string";
  private final String otherString = "my string 2";
  private HashCode hash;

  @Test
  public void type_of_sstring_is_sstring() throws Exception {
    given(sstring = string(string));
    when(sstring).type();
    thenReturned(stringType());
  }

  @Test
  public void data_returns_java_string() throws Exception {
    given(sstring = string(string));
    when(sstring).data();
    thenReturned(string);
  }

  @Test
  public void data_returns_empty_java_string_for_empty_sstring() throws Exception {
    given(sstring = string(""));
    when(sstring).data();
    thenReturned("");
  }

  @Test
  public void sstrings_with_equal_values_are_equal() throws Exception {
    when(string(string));
    thenReturned(string(string));
  }

  @Test
  public void sstrings_with_different_values_are_not_equal() throws Exception {
    when(string(string));
    thenReturned(not(string(otherString)));
  }

  @Test
  public void hash_of_sstrings_with_equal_values_is_the_same() throws Exception {
    given(sstring = string(string));
    when(sstring).hash();
    thenReturned(string(string).hash());
  }

  @Test
  public void hash_of_sstrings_with_different_values_is_not_the_same() throws Exception {
    given(sstring = string(string));
    when(sstring).hash();
    thenReturned(not(string(string + "abc").hash()));
  }

  @Test
  public void hash_code_of_sstrings_with_equal_values_is_the_same() throws Exception {
    given(sstring = string(string));
    when(sstring).hashCode();
    thenReturned(string(string).hashCode());
  }

  @Test
  public void hash_code_of_sstrings_with_different_values_is_not_the_same() throws Exception {
    given(sstring = string(string));
    when(sstring).hashCode();
    thenReturned(not(string(string + "abc").hashCode()));
  }

  @Test
  public void sstring_can_be_read_back_by_hash() throws Exception {
    given(sstring = string(string));
    given(hash = sstring.hash());
    when(() -> valuesDbOther().get(hash));
    thenReturned(sstring);
  }

  @Test
  public void sstring_read_back_by_hash_has_same_data() throws Exception {
    given(sstring = string(string));
    given(hash = sstring.hash());
    when(() -> ((SString) valuesDbOther().get(hash)).data());
    thenReturned(string);
  }

  @Test
  public void to_string_contains_string_value() throws Exception {
    given(sstring = string(string));
    when(() -> sstring.toString());
    thenReturned("String(...):" + sstring.hash());
  }

  @Test
  public void reading_not_stored_sstring_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(sstring = stringType().newValue(hash));
    when(sstring).data();
    thenThrown(ValuesDbException.class);
  }
}
