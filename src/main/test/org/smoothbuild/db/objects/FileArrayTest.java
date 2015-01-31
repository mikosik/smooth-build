package org.smoothbuild.db.objects;

import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashError;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class FileArrayTest {
  private ObjectsDb objectsDb;
  private Array<?> array;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestObjectsDbModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
  }

  @Test
  public void type_of_file_array_is_file_array() throws Exception {
    given(array = objectsDb.arrayBuilder(SFile.class).build());
    when(array.type());
    thenReturned(FILE_ARRAY);
  }

  @Test
  public void reading_elements_from_not_stored_file_array_fails() throws Exception {
    given(array = (Array<SFile>) objectsDb.read(FILE_ARRAY, HashCode.fromInt(33)));
    when(array).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }
}
