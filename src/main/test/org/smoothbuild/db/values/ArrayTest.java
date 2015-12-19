package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.smoothbuild.testing.db.values.ValueCreators.blob;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;

public class ArrayTest {
  private ValuesDb valuesDb;
  private Blob blob;
  private SString sstring;
  private SString sstring2;
  private SString sstring3;
  private Array<?> array;
  private ArrayBuilder<SString> arrayBuilder;
  @SuppressWarnings("rawtypes")
  private ArrayBuilder rawArrayBuilder;

  @Before
  public void before() {
    valuesDb = memoryValuesDb();
  }

  @Test
  public void empty_array_is_empty() throws Exception {
    when(valuesDb.arrayBuilder(SString.class).build());
    thenReturned(emptyIterable());
  }

  @Test
  public void adding_null_is_forbidden() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    when(arrayBuilder).add(sstring);
    thenThrown(NullPointerException.class);
  }

  @Test
  public void adding_element_with_wrong_smooth_type_is_forbidden() throws Exception {
    given(rawArrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(blob = blob(memoryValuesDb(), "content"));
    when(rawArrayBuilder).add(blob);
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_element() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(sstring = valuesDb.string("abc"));
    given(arrayBuilder).add(sstring);
    when(arrayBuilder).build();
    thenReturned(contains(sstring));
  }

  @Test
  public void array_contains_added_elements_in_correct_order() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    given(sstring3 = valuesDb.string("ghi"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(arrayBuilder).add(sstring3);
    when(arrayBuilder.build());
    thenReturned(contains(sstring, sstring2, sstring3));
  }

  @Test
  public void adding_same_element_twice_builds_array_with_two_elements() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(sstring = valuesDb.string("abc"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring);
    when(arrayBuilder.build());
    thenReturned(contains(sstring, sstring));
  }

  @Test
  public void arrays_with_same_elements_have_same_hash() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    when(valuesDb.arrayBuilder(SString.class).add(sstring).add(sstring2).build().hash());
    thenReturned(valuesDb.arrayBuilder(SString.class).add(sstring).add(sstring2).build().hash());
  }

  @Test
  public void one_element_array_hash_is_different_than_its_element_hash() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(sstring = valuesDb.string("abc"));
    given(arrayBuilder).add(sstring);
    given(array = arrayBuilder.build());
    when(array.hash());
    thenReturned(not(sstring.hash()));
  }

  @Test
  public void arrays_with_same_elements_but_in_different_order_have_different_hashes()
      throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    when(valuesDb.arrayBuilder(SString.class).add(sstring).add(sstring2).build().hash());
    thenReturned(not(valuesDb.arrayBuilder(SString.class).add(sstring2).add(sstring).build()
        .hash()));
  }

  @Test
  public void array_with_one_more_element_have_different_hash() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    when(valuesDb.arrayBuilder(SString.class).add(sstring).build().hash());
    thenReturned(not(valuesDb.arrayBuilder(SString.class).add(sstring2).add(sstring).build()
        .hash()));
  }

  @Test
  public void array_can_be_read_back() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(arrayBuilder).add(valuesDb.string("abc"));
    given(arrayBuilder).add(valuesDb.string("def"));
    given(array = arrayBuilder.build());
    when(valuesDb.read(STRING_ARRAY, array.hash()));
    thenReturned(array);
  }

  @Test
  public void array_read_back_contains_same_elements() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(array = arrayBuilder.build());
    when(valuesDb.read(STRING_ARRAY, array.hash()));
    thenReturned(contains(sstring, sstring2));
  }

  @Test
  public void array_read_back_has_same_hash() throws Exception {
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(array = arrayBuilder.build());
    when(valuesDb.read(STRING_ARRAY, array.hash()).hash());
    thenReturned(array.hash());
  }

  @Test
  public void to_string_contains_all_elements_in_square_brackets() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(sstring2 = valuesDb.string("def"));
    given(arrayBuilder = valuesDb.arrayBuilder(SString.class));
    given(arrayBuilder).add(sstring);
    given(arrayBuilder).add(sstring2);
    given(array = arrayBuilder.build());
    when(array).toString();
    thenReturned("[abc, def]");
  }

}
