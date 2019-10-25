package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.emptyIterable;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.testing.TestingContext;

public class NilTest extends TestingContext {
  private Array array;

  @Test
  public void type_of_nil_is_nil() throws Exception {
    given(array = arrayBuilder(nothingType()).build());
    when(array.type());
    thenReturned(arrayType(nothingType()));
  }

  @Test
  public void nil_array_is_empty() throws Exception {
    when(() -> arrayBuilder(nothingType()).build().asIterable(Value.class));
    thenReturned(emptyIterable());
  }

  @Test
  public void nil_can_be_read_by_hash() throws Exception {
    given(array = arrayBuilder(nothingType()).build());
    when(() -> valuesDbOther().get(array.hash()));
    thenReturned(array);
  }

  @Test
  public void nil_read_by_hash_has_no_elements() throws Exception {
    given(array = arrayBuilder(nothingType()).build());
    when(() -> ((Array) valuesDbOther().get(array.hash())).asIterable(Value.class));
    thenReturned(emptyIterable());
  }

  @Test
  public void nil_to_string() throws Exception {
    given(array = arrayBuilder(nothingType()).build());
    when(() -> array.toString());
    thenReturned("[Nothing](...):" + array.hash());
  }
}
