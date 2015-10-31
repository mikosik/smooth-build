package org.smoothbuild.db.values;

import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashError;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BlobArrayTest {
  private ValuesDb valuesDb;
  private Array<?> array;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestValuesDbModule());
    valuesDb = injector.getInstance(ValuesDb.class);
  }

  @Test
  public void type_of_blob_array_is_blob_array() throws Exception {
    given(array = valuesDb.arrayBuilder(Blob.class).build());
    when(array.type());
    thenReturned(BLOB_ARRAY);
  }

  @Test
  public void reading_elements_from_not_stored_blob_array_fails() throws Exception {
    given(array = (Array<Blob>) valuesDb.read(BLOB_ARRAY, HashCode.fromInt(33)));
    when(array).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }
}
