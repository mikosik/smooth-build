package org.smoothbuild.db.values;

import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashException;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class FileArrayTest {
  private ValuesDb valuesDb;
  private Array<?> array;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestValuesDbModule());
    valuesDb = injector.getInstance(ValuesDb.class);
  }

  @Test
  public void type_of_file_array_is_file_array() throws Exception {
    given(array = valuesDb.arrayBuilder(SFile.class).build());
    when(array.type());
    thenReturned(FILE_ARRAY);
  }

  @Test
  public void reading_elements_from_not_stored_file_array_fails() throws Exception {
    given(array = (Array<SFile>) valuesDb.read(FILE_ARRAY, HashCode.fromInt(33)));
    when(array).iterator();
    thenThrown(NoObjectWithGivenHashException.class);
  }
}
