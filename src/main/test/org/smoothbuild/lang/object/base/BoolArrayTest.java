package org.smoothbuild.lang.object.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.testing.TestingContext;

public class BoolArrayTest extends TestingContext {
  private Array array;
  private Hash hash;

  @Test
  public void type_of_bool_array_is_bool_array() throws Exception {
    given(array = arrayBuilder(boolType()).build());
    when(array.type());
    thenReturned(arrayType(boolType()));
  }

  @Test
  public void reading_elements_from_not_stored_bool_array_fails() throws Exception {
    given(hash = Hash.of(33));
    given(array = arrayType(boolType()).newInstance(hash));
    when(array).asIterable(Bool.class);
    thenThrown(ObjectsDbException.class);
  }
}