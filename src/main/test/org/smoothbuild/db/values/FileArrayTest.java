package org.smoothbuild.db.values;

import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.value.Array;

import com.google.common.hash.HashCode;

public class FileArrayTest {
  private ValuesDb valuesDb;
  private Array array;
  private HashCode hash;

  @Before
  public void before() {
    valuesDb = memoryValuesDb();
  }

  @Test
  public void type_of_file_array_is_file_array() throws Exception {
    given(array = valuesDb.arrayBuilder(FILE).build());
    when(array.type());
    thenReturned(FILE_ARRAY);
  }

  @Test
  public void reading_elements_from_not_stored_file_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = valuesDb.read(FILE_ARRAY, hash));
    when(array).iterator();
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }
}
