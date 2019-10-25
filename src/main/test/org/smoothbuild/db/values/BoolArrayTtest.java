package org.smoothbuild.db.values;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.testing.TestingContext;

import com.google.common.hash.HashCode;

public class BoolArrayTtest extends TestingContext {
  private Array array;
  private HashCode hash;

  @Test
  public void type_of_bool_array_is_bool_array() throws Exception {
    given(array = arrayBuilder(boolType()).build());
    when(array.type());
    thenReturned(arrayType(boolType()));
  }

  @Test
  public void reading_elements_from_not_stored_bool_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = arrayType(boolType()).newValue(hash));
    when(array).asIterable(Bool.class);
    thenThrown(ValuesDbException.class);
  }
}