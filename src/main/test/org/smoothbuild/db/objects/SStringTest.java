package org.smoothbuild.db.objects;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class SStringTest {
  private ObjectsDb objectsDb;
  private SString sstring;
  private final String string = "my string";
  private final String otherString = "my string 2";
  private HashCode hash;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestObjectsDbModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
  }

  @Test
  public void type_of_sstring_is_sstring() throws Exception {
    given(sstring = objectsDb.string(string));
    when(sstring).type();
    thenReturned(STRING);
  }

  @Test
  public void value_returns_java_string() throws Exception {
    given(sstring = objectsDb.string(string));
    when(sstring).value();
    thenReturned(string);
  }

  @Test
  public void value_returns_empty_java_string_for_empty_sstring() throws Exception {
    given(sstring = objectsDb.string(""));
    when(sstring).value();
    thenReturned("");
  }

  @Test
  public void sstrings_with_equal_values_are_equal() throws Exception {
    when(objectsDb.string(string));
    thenReturned(objectsDb.string(string));
  }

  @Test
  public void sstrings_with_different_values_are_not_equal() throws Exception {
    when(objectsDb.string(string));
    thenReturned(not(objectsDb.string(otherString)));
  }

  @Test
  public void hash_of_sstrings_with_equal_values_is_the_same() throws Exception {
    given(sstring = objectsDb.string(string));
    when(sstring).hash();
    thenReturned(objectsDb.string(string).hash());
  }

  @Test
  public void hash_of_sstrings_with_different_values_is_not_the_same() throws Exception {
    given(sstring = objectsDb.string(string));
    when(sstring).hash();
    thenReturned(not(objectsDb.string(string + "abc").hash()));
  }

  @Test
  public void hash_code_of_sstrings_with_equal_values_is_the_same() throws Exception {
    given(sstring = objectsDb.string(string));
    when(sstring).hashCode();
    thenReturned(objectsDb.string(string).hashCode());
  }

  @Test
  public void hash_code_of_sstrings_with_different_values_is_not_the_same() throws Exception {
    given(sstring = objectsDb.string(string));
    when(sstring).hashCode();
    thenReturned(not(objectsDb.string(string + "abc").hashCode()));
  }

  @Test
  public void sstring_can_be_fetch_by_hash() throws Exception {
    given(sstring = objectsDb.string(string));
    given(hash = sstring.hash());
    when(objectsDb.read(STRING, hash));
    thenReturned(sstring);
  }

  @Test
  public void sstring_fetched_by_hash_has_same_value() throws Exception {
    given(sstring = objectsDb.string(string));
    given(hash = sstring.hash());
    when(((SString) objectsDb.read(STRING, hash)).value());
    thenReturned(string);
  }

  @Test
  public void to_string_contains_string_value() throws Exception {
    given(sstring = objectsDb.string(string));
    when(sstring).toString();
    thenReturned(string);
  }
}
