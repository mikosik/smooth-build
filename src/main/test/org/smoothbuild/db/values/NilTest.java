package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.emptyIterable;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Nothing;

public class NilTest {
  private ValuesDb valuesDb;
  private Array<?> array;

  @Before
  public void before() {
    valuesDb = memoryValuesDb();
  }

  @Test
  public void type_of_nil_is_nil() throws Exception {
    given(array = valuesDb.arrayBuilder(Nothing.class).build());
    when(array.type());
    thenReturned(NIL);
  }

  @Test
  public void nil_array_is_empty() throws Exception {
    when(valuesDb.arrayBuilder(Nothing.class).build());
    thenReturned(emptyIterable());
  }

  @Test
  public void nil_to_string_contains_square_brackets() throws Exception {
    when(valuesDb.arrayBuilder(Nothing.class).build().toString());
    thenReturned("[]");
  }

}
