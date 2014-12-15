package org.smoothbuild.db.objects;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.base.Types.FILE_ARRAY;
import static org.smoothbuild.lang.base.Types.NIL;
import static org.smoothbuild.lang.base.Types.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.Nothing;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Types;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ArrayTest {
  private ObjectsDb objectsDb;
  private Blob blob;
  private SString sstring;
  private SString sstring2;
  private SString sstring3;
  private Array<?> array;
  private ArrayBuilder<Nothing> nilBuilder;
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
    given(array = objectsDb.arrayBuilder(SString.class).build());
    when(array.type());
    thenReturned(STRING_ARRAY);
  }

  @Test
  public void type_of_blob_array_is_blob_array() throws Exception {
    given(array = objectsDb.arrayBuilder(Blob.class).build());
    when(array.type());
    thenReturned(BLOB_ARRAY);
  }

  @Test
  public void type_of_file_array_is_file_array() throws Exception {
    given(array = objectsDb.arrayBuilder(SFile.class).build());
    when(array.type());
    thenReturned(FILE_ARRAY);
  }

  @Test
  public void type_of_nil_is_nil() throws Exception {
    given(array = objectsDb.arrayBuilder(Nothing.class).build());
    when(array.type());
    thenReturned(NIL);
  }

  @Test
  public void adding_elements_to_nil_is_forbidden() throws Exception {
    given(nilBuilder = objectsDb.arrayBuilder(Nothing.class));
    when(nilBuilder).add(new MyNothing());
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void empty_array_is_empty() throws Exception {
    when(objectsDb.arrayBuilder(SString.class).build());
    thenReturned(emptyIterable());
  }

  @Test
  public void nil_array_is_empty() throws Exception {
    when(objectsDb.arrayBuilder(Nothing.class).build());
    thenReturned(emptyIterable());
  }

  @Test
  public void adding_null_is_forbidden() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
    when(arrayBuilder).add(sstring);
    thenThrown(NullPointerException.class);
  }

  @Test
  public void adding_element_with_wrong_smooth_type_is_forbidden() throws Exception {
    given(rawArrayBuilder = objectsDb.arrayBuilder(SString.class));
    given(blob = new FakeObjectsDb().blob("content"));
    when(rawArrayBuilder).add(blob);
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_element() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
    given(sstring = objectsDb.string("abc"));
    given(arrayBuilder).add(sstring);
    when(arrayBuilder).build();
    thenReturned(contains(sstring));
  }

  @Test
  public void array_contains_added_elements_in_correct_order() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
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
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
    given(sstring = objectsDb.string("abc"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring);
    when(arrayBuilder.build());
    thenReturned(contains(sstring, sstring));
  }

  @Test
  public void arrays_with_same_elements_have_same_hash() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
    given(sstring = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    when(objectsDb.arrayBuilder(SString.class).add(sstring).add(sstring2).build().hash());
    thenReturned(objectsDb.arrayBuilder(SString.class).add(sstring).add(sstring2).build().hash());
  }

  @Test
  public void one_element_array_hash_is_different_than_its_element_hash() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
    given(sstring = objectsDb.string("abc"));
    given(arrayBuilder).add(sstring);
    given(array = arrayBuilder.build());
    when(array.hash());
    thenReturned(not(sstring.hash()));
  }

  @Test
  public void arrays_with_same_elements_but_in_different_order_have_different_hashes()
      throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
    given(sstring = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    when(objectsDb.arrayBuilder(SString.class).add(sstring).add(sstring2).build().hash());
    thenReturned(not(objectsDb.arrayBuilder(SString.class).add(sstring2).add(sstring).build()
        .hash()));
  }

  @Test
  public void array_with_one_more_element_have_different_hash() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
    given(sstring = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    when(objectsDb.arrayBuilder(SString.class).add(sstring).build().hash());
    thenReturned(not(objectsDb.arrayBuilder(SString.class).add(sstring2).add(sstring).build()
        .hash()));
  }

  @Test
  public void array_can_be_read_back() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
    given(arrayBuilder).add(objectsDb.string("abc"));
    given(arrayBuilder).add(objectsDb.string("def"));
    given(array = arrayBuilder.build());
    when(objectsDb.read(STRING_ARRAY, array.hash()));
    thenReturned(array);
  }

  @Test
  public void array_read_back_contains_same_elements() throws Exception {
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
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
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
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
    given(arrayBuilder = objectsDb.arrayBuilder(SString.class));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(array = arrayBuilder.build());
    when(array).toString();
    thenReturned("[abc, def]");
  }

  @Test
  public void nil_to_string_contains_square_brackets() throws Exception {
    when(objectsDb.arrayBuilder(Nothing.class).build().toString());
    thenReturned("[]");
  }

  private static class MyNothing implements Nothing {
    @Override
    public HashCode hash() {
      return Hash.string("");
    }

    @Override
    public Type type() {
      return Types.NOTHING;
    }
  }
}
