package org.smoothbuild.db.values;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.testing.TestingContext;

public class StringArrayTest extends TestingContext {
  private Array array;
  private Hash hash;

  @Test
  public void type_of_string_array_is_string_array() throws Exception {
    given(array = arrayBuilder(stringType()).build());
    when(array.type());
    thenReturned(arrayType(stringType()));
  }

  @Test
  public void reading_elements_from_not_stored_string_array_fails() throws Exception {
    given(hash = Hash.of(33));
    given(array = arrayType(stringType()).newValue(hash));
    when(array).asIterable(SString.class);
    thenThrown(ValuesDbException.class);
  }
}
