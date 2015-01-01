package org.smoothbuild.db.objects;

import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashError;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ObjectsDbTest {
  private Array<SFile> fileArray;
  private Array<Blob> blobArray;
  private Blob blob;
  private Array<SString> stringArray;
  private SString stringValue;

  private ObjectsDb objectsDb;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestObjectsDbModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
  }

  @Test
  public void reading_elements_from_not_stored_file_array_fails() throws Exception {
    given(fileArray = (Array<SFile>) objectsDb.read(FILE_ARRAY, HashCode.fromInt(33)));
    when(fileArray).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  @Test
  public void reading_elements_from_not_stored_blob_array_fails() throws Exception {
    given(blobArray = (Array<Blob>) objectsDb.read(BLOB_ARRAY, HashCode.fromInt(33)));
    when(blobArray).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  @Test
  public void reading_elements_from_not_stored_string_array_fails() throws Exception {
    given(stringArray = (Array<SString>) objectsDb.read(STRING_ARRAY, HashCode.fromInt(33)));
    when(stringArray).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  @Test
  public void reading_not_stored_file_fails() throws Exception {
    when(objectsDb).read(FILE, HashCode.fromInt(33));
    thenThrown(NoObjectWithGivenHashError.class);
  }

  @Test
  public void reading_not_stored_blob_fails() throws Exception {
    given(blob = (Blob) objectsDb.read(BLOB, HashCode.fromInt(33)));
    when(blob).openInputStream();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  @Test
  public void reading_not_stored_sstring_fails() throws Exception {
    given(stringValue = (SString) objectsDb.read(STRING, HashCode.fromInt(33)));
    when(stringValue).value();
    thenThrown(NoObjectWithGivenHashError.class);
  }
}
