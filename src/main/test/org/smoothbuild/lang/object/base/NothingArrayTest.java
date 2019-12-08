package org.smoothbuild.lang.object.base;

import static org.hamcrest.Matchers.emptyIterable;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.testing.TestingContext;

public class NothingArrayTest extends TestingContext {
  private Array array;

  @Test
  public void type_of_nothing_array_is_nothing_array() throws Exception {
    given(array = arrayBuilder(nothingType()).build());
    when(array.type());
    thenReturned(arrayType(nothingType()));
  }

  @Test
  public void nothing_array_is_empty() throws Exception {
    when(() -> arrayBuilder(nothingType()).build().asIterable(SObject.class));
    thenReturned(emptyIterable());
  }

  @Test
  public void nothing_array_can_be_read_by_hash() throws Exception {
    given(array = arrayBuilder(nothingType()).build());
    when(() -> objectDbOther().get(array.hash()));
    thenReturned(array);
  }

  @Test
  public void nothing_array_read_by_hash_has_no_elements() throws Exception {
    given(array = arrayBuilder(nothingType()).build());
    when(() -> ((Array) objectDbOther().get(array.hash())).asIterable(SObject.class));
    thenReturned(emptyIterable());
  }

  @Test
  public void nothing_array_to_string() throws Exception {
    given(array = arrayBuilder(nothingType()).build());
    when(() -> array.toString());
    thenReturned("[Nothing](...):" + array.hash());
  }
}
