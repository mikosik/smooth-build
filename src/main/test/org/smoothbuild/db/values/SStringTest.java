package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class SStringTest {
  private TypeSystem typeSystem;
  private ValuesDb valuesDb;
  private SString sstring;
  private final String string = "my string";
  private final String otherString = "my string 2";
  private HashCode hash;

  @Before
  public void before() {
    HashedDb hashedDb = new HashedDb();
    typeSystem = new TypeSystem(new TypesDb(hashedDb));
    valuesDb = new ValuesDb(hashedDb, typeSystem);
  }

  @Test
  public void type_of_sstring_is_sstring() throws Exception {
    given(sstring = valuesDb.string(string));
    when(sstring).type();
    thenReturned(typeSystem.string());
  }

  @Test
  public void value_returns_java_string() throws Exception {
    given(sstring = valuesDb.string(string));
    when(sstring).value();
    thenReturned(string);
  }

  @Test
  public void value_returns_empty_java_string_for_empty_sstring() throws Exception {
    given(sstring = valuesDb.string(""));
    when(sstring).value();
    thenReturned("");
  }

  @Test
  public void sstrings_with_equal_values_are_equal() throws Exception {
    when(valuesDb.string(string));
    thenReturned(valuesDb.string(string));
  }

  @Test
  public void sstrings_with_different_values_are_not_equal() throws Exception {
    when(valuesDb.string(string));
    thenReturned(not(valuesDb.string(otherString)));
  }

  @Test
  public void hash_of_sstrings_with_equal_values_is_the_same() throws Exception {
    given(sstring = valuesDb.string(string));
    when(sstring).hash();
    thenReturned(valuesDb.string(string).hash());
  }

  @Test
  public void hash_of_sstrings_with_different_values_is_not_the_same() throws Exception {
    given(sstring = valuesDb.string(string));
    when(sstring).hash();
    thenReturned(not(valuesDb.string(string + "abc").hash()));
  }

  @Test
  public void hash_code_of_sstrings_with_equal_values_is_the_same() throws Exception {
    given(sstring = valuesDb.string(string));
    when(sstring).hashCode();
    thenReturned(valuesDb.string(string).hashCode());
  }

  @Test
  public void hash_code_of_sstrings_with_different_values_is_not_the_same() throws Exception {
    given(sstring = valuesDb.string(string));
    when(sstring).hashCode();
    thenReturned(not(valuesDb.string(string + "abc").hashCode()));
  }

  @Test
  public void sstring_can_be_fetch_by_hash() throws Exception {
    given(sstring = valuesDb.string(string));
    given(hash = sstring.hash());
    when(typeSystem.string().newValue(hash));
    thenReturned(sstring);
  }

  @Test
  public void sstring_fetched_by_hash_has_same_value() throws Exception {
    given(sstring = valuesDb.string(string));
    given(hash = sstring.hash());
    when(typeSystem.string().newValue(hash).value());
    thenReturned(string);
  }

  @Test
  public void to_string_contains_string_value() throws Exception {
    given(sstring = valuesDb.string(string));
    when(sstring).toString();
    thenReturned(string);
  }

  @Test
  public void reading_not_stored_sstring_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(sstring = typeSystem.string().newValue(hash));
    when(sstring).value();
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }
}
