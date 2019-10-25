package org.smoothbuild.db.values;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.testing.TestingContext;

import com.google.common.hash.HashCode;

public class StructArrayTest extends TestingContext {
  private Array array;
  private HashCode hash;

  @Test
  public void type_of_struct_array_is_struct_array() throws Exception {
    given(array = arrayBuilder(personType()).build());
    when(array.type());
    thenReturned(arrayType(personType()));
  }

  @Test
  public void reading_elements_from_not_stored_struct_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = arrayType(personType()).newValue(hash));
    when(array).asIterable(Struct.class);
    thenThrown(ValuesDbException.class);
  }
}
