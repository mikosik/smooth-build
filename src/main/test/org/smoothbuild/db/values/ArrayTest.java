package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

public class ArrayTest {
  private HashedDb hashedDb;
  private ValuesDb valuesDb;
  private Blob blob;
  private SString sstring;
  private SString sstring2;
  private SString sstring3;
  private Array array;
  private ArrayBuilder arrayBuilder;
  private TypesDb typesDb;

  @Before
  public void before() {
    hashedDb = new TestingHashedDb();
    typesDb = new TypesDb(hashedDb);
    valuesDb = new ValuesDb(hashedDb, typesDb);
  }

  @Test
  public void empty_nothing_array_can_be_iterated_as_struct() {
    given(array = valuesDb.arrayBuilder(typesDb.nothing()).build());
    when(() -> array.asIterable(Struct.class).iterator().hasNext());
    thenReturned(false);
  }

  @Test
  public void string_array_cannot_be_iterated_as_struct() {
    given(sstring = valuesDb.string("abc"));
    given(array = valuesDb.arrayBuilder(typesDb.string()).add(sstring).build());
    when(() -> array.asIterable(Struct.class).iterator().hasNext());
    thenThrown(exception(new IllegalArgumentException(
        "Array of type '[String]' cannot be iterated as " + Struct.class.getCanonicalName())));
  }

  @Test
  public void empty_array_is_empty() throws Exception {
    when(() -> valuesDb.arrayBuilder(typesDb.string()).build().asIterable(SString.class));
    thenReturned(emptyIterable());
  }

  @Test
  public void adding_null_is_forbidden() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    when(arrayBuilder).add(null);
    thenThrown(NullPointerException.class);
  }

  @Test
  public void adding_element_with_wrong_smooth_type_is_forbidden() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(blob = mock(Blob.class));
    given(willReturn(typesDb.blob()), blob).type();
    when(arrayBuilder).add(blob);
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_element() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(sstring = valuesDb.string("abc"));
    given(arrayBuilder).add(sstring);
    when(() -> arrayBuilder.build().asIterable(SString.class));
    thenReturned(contains(sstring));
  }

  @Test
  public void array_contains_added_element_via_add_all_method() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(sstring = valuesDb.string("abc"));
    given(arrayBuilder).addAll(list(sstring, sstring));
    when(() -> arrayBuilder.build().asIterable(SString.class));
    thenReturned(contains(sstring, sstring));
  }

  @Test
  public void array_contains_added_elements_in_correct_order() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    given(sstring3 = valuesDb.string("ghi"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(arrayBuilder).add(sstring3);
    when(() -> arrayBuilder.build().asIterable(SString.class));
    thenReturned(contains(sstring, sstring2, sstring3));
  }

  @Test
  public void adding_same_element_twice_builds_array_with_two_elements() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(sstring = valuesDb.string("abc"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring);
    when(() -> arrayBuilder.build().asIterable(SString.class));
    thenReturned(contains(sstring, sstring));
  }

  @Test
  public void arrays_with_same_elements_have_same_hash() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    when(valuesDb.arrayBuilder(typesDb.string()).add(sstring).add(sstring2).build().hash());
    thenReturned(valuesDb.arrayBuilder(typesDb.string()).add(sstring).add(sstring2).build()
        .hash());
  }

  @Test
  public void one_element_array_hash_is_different_than_its_element_hash() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(sstring = valuesDb.string("abc"));
    given(arrayBuilder).add(sstring);
    given(array = arrayBuilder.build());
    when(array.hash());
    thenReturned(not(sstring.hash()));
  }

  @Test
  public void arrays_with_same_elements_but_in_different_order_have_different_hashes()
      throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    when(valuesDb.arrayBuilder(typesDb.string()).add(sstring).add(sstring2).build().hash());
    thenReturned(not(valuesDb.arrayBuilder(typesDb.string()).add(sstring2).add(sstring).build()
        .hash()));
  }

  @Test
  public void array_with_one_more_element_have_different_hash() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    when(valuesDb.arrayBuilder(typesDb.string()).add(sstring).build().hash());
    thenReturned(not(valuesDb.arrayBuilder(typesDb.string()).add(sstring2).add(sstring).build()
        .hash()));
  }

  @Test
  public void array_can_be_read_by_hash() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(arrayBuilder).add(valuesDb.string("abc"));
    given(arrayBuilder).add(valuesDb.string("def"));
    given(array = arrayBuilder.build());
    when(() -> new TestingValuesDb(hashedDb).get(array.hash()));
    thenReturned(array);
  }

  @Test
  public void array_read_by_hash_contains_same_elements() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(array = arrayBuilder.build());
    when(() -> ((Array) new TestingValuesDb(hashedDb).get(array.hash())).asIterable(SString.class));
    thenReturned(contains(sstring, sstring2));
  }

  @Test
  public void array_read_by_hash_has_same_hash() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(array = arrayBuilder.build());
    when(() -> new TestingValuesDb(hashedDb).get(array.hash()).hash());
    thenReturned(array.hash());
  }

  @Test
  public void to_string() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    given(arrayBuilder = valuesDb.arrayBuilder(typesDb.string()));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(array = arrayBuilder.build());
    when(() -> array.toString());
    thenReturned("[String](...):" + array.hash());
  }
}
