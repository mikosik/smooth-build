package org.smoothbuild.testing.lang.type;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

import com.google.common.collect.Iterables;

public class FakeArrayTest {
  FakeString sstring1;
  FakeString sstring2;
  FakeArray<SString> array;

  @Test
  public void type() throws Exception {
    given(array = new FakeArray<>(STRING_ARRAY));
    when(array.type());
    thenReturned(STRING_ARRAY);
  }

  @Test
  public void initially_array_is_empty() throws Exception {
    given(array = new FakeArray<>(STRING_ARRAY));
    when(Iterables.size(array));
    thenReturned(0);
  }

  @Test
  public void fake_array_contains_added_element() throws Exception {
    given(array = new FakeArray<>(STRING_ARRAY));
    given(sstring1 = new FakeString("my string"));
    when(array).add(sstring1);
    then(array, contains(sstring1));
  }

  @Test
  public void fake_array_contains_elements_passed_to_creation_method() throws Exception {
    given(sstring1 = new FakeString("my string"));
    given(sstring2 = new FakeString("my string B"));
    when(FakeArray.fakeArray(STRING_ARRAY, sstring1, sstring2));
    thenReturned(contains(sstring1, sstring2));
  }

  @Test
  public void fake_array_hash_is_compatible_with_array_object_hash() throws Exception {
    given(sstring1 = new FakeString("my string"));
    given(sstring2 = new FakeString("my string B"));
    given(array = FakeArray.fakeArray(STRING_ARRAY, sstring1, sstring2));
    when(array).hash();
    thenReturned(objectArray(sstring1, sstring2).hash());
  }

  private static SArray<SString> objectArray(SString elem1, SString elem2) {
    return new FakeObjectsDb().arrayBuilder(STRING_ARRAY).add(elem1).add(elem2).build();
  }
}
