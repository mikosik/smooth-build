package org.smoothbuild.db.objects;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.STypes;
import org.smoothbuild.lang.base.SValue;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class SArrayTest {
  private ObjectsDb objectsDb;
  private SString sstring;
  private SString sstring2;
  private SString sstring3;
  private SArray<?> array;
  private ArrayBuilder<SNothing> nilBuilder;
  private ArrayBuilder<SString> arrayBuilder;
  @SuppressWarnings("rawtypes")
  private ArrayBuilder rawArrayBuilder;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestObjectsDbModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
  }

  @Test
  public void type_of_string_array_is_string_array() throws Exception {
    given(array = objectsDb.arrayBuilder(STRING_ARRAY).build());
    when(array.type());
    thenReturned(STRING_ARRAY);
  }

  @Test
  public void type_of_blob_array_is_blob_array() throws Exception {
    given(array = objectsDb.arrayBuilder(BLOB_ARRAY).build());
    when(array.type());
    thenReturned(BLOB_ARRAY);
  }

  @Test
  public void type_of_file_array_is_file_array() throws Exception {
    given(array = objectsDb.arrayBuilder(FILE_ARRAY).build());
    when(array.type());
    thenReturned(FILE_ARRAY);
  }

  @Test
  public void type_of_nil_is_nil() throws Exception {
    given(array = objectsDb.arrayBuilder(NIL).build());
    when(array.type());
    thenReturned(NIL);
  }

  @Test
  public void adding_elements_to_nil_is_forbidden() throws Exception {
    given(nilBuilder = objectsDb.arrayBuilder(NIL));
    when(nilBuilder).add(new MyNothing());
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void empty_array_is_empty() throws Exception {
    when(objectsDb.arrayBuilder(STRING_ARRAY).build());
    thenReturned(emptyIterable());
  }

  @Test
  public void nil_array_is_empty() throws Exception {
    when(objectsDb.arrayBuilder(NIL).build());
    thenReturned(emptyIterable());
  }

  @Test
  public void adding_null_is_forbidden() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    when(arrayBuilder).add(sstring);
    thenThrown(NullPointerException.class);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void adding_element_with_wrong_smooth_type_is_forbidden() throws Exception {
    given(rawArrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    when(rawArrayBuilder).add(new MyNothing());
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_element() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(sstring = objectsDb.string("abc"));
    given(arrayBuilder).add(sstring);
    when(arrayBuilder).build();
    thenReturned(contains(sstring));
  }

  @Test
  public void array_contains_added_elements_in_correct_order() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(sstring = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    given(sstring3 = objectsDb.string("ghi"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(arrayBuilder).add(sstring3);
    when(arrayBuilder.build());
    thenReturned(contains(sstring, sstring2, sstring3));
  }

  @Test
  public void adding_same_element_twice_builds_array_with_two_elements() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(sstring = objectsDb.string("abc"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring);
    when(arrayBuilder.build());
    thenReturned(contains(sstring, sstring));
  }

  @Test
  public void arrays_with_same_elements_have_same_hash() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(sstring = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    when(objectsDb.arrayBuilder(STRING_ARRAY).add(sstring).add(sstring2).build().hash());
    thenReturned(objectsDb.arrayBuilder(STRING_ARRAY).add(sstring).add(sstring2).build().hash());
  }

  @Test
  public void one_element_array_hash_is_different_than_its_element_hash() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(sstring = objectsDb.string("abc"));
    given(arrayBuilder).add(sstring);
    given(array = arrayBuilder.build());
    when(array.hash());
    thenReturned(not(sstring.hash()));
  }

  @Test
  public void arrays_with_same_elements_but_in_different_order_have_different_hashes()
      throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(sstring = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    when(objectsDb.arrayBuilder(STRING_ARRAY).add(sstring).add(sstring2).build().hash());
    thenReturned(not(objectsDb.arrayBuilder(STRING_ARRAY).add(sstring2).add(sstring).build().hash()));
  }

  @Test
  public void array_with_one_more_element_have_different_hash() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(sstring = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    when(objectsDb.arrayBuilder(STRING_ARRAY).add(sstring).build().hash());
    thenReturned(not(objectsDb.arrayBuilder(STRING_ARRAY).add(sstring2).add(sstring).build().hash()));
  }

  @Test
  public void array_can_be_read_back() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(arrayBuilder).add(objectsDb.string("abc"));
    given(arrayBuilder).add(objectsDb.string("def"));
    given(array = arrayBuilder.build());
    when(objectsDb.read(STRING_ARRAY, array.hash()));
    thenReturned(array);
  }

  @Test
  public void array_read_back_contains_same_elements() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(sstring = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(array = arrayBuilder.build());
    when(objectsDb.read(STRING_ARRAY, array.hash()));
    thenReturned(contains(sstring, sstring2));
  }

  @Test
  public void array_read_back_has_same_hash() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(sstring = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(array = arrayBuilder.build());
    when(objectsDb.read(STRING_ARRAY, array.hash()).hash());
    thenReturned(array.hash());
  }

  @Test
  public void to_string_contains_all_elements_in_square_brackets() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    given(arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(array = arrayBuilder.build());
    when(array).toString();
    thenReturned("[abc, def]");
  }

  @Test
  public void nil_to_string_contains_square_brackets() throws Exception {
    when(objectsDb.arrayBuilder(NIL).build().toString());
    thenReturned("[]");
  }

  private static class MyNothing implements SNothing {
    @Override
    public HashCode hash() {
      return Hash.string("");
    }

    @Override
    public SType<? extends SValue> type() {
      return STypes.NOTHING;
    }
  }
}
