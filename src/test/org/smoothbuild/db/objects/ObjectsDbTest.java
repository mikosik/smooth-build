package org.smoothbuild.db.objects;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashError;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ObjectsDbTest {
  private SArray<SFile> fileArray;
  private SArray<SBlob> blobArray;
  private SBlob blob;
  private SArray<SString> stringArray;
  private SString stringValue;

  private ObjectsDb objectsDb;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestObjectsDbModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
  }

  @Test
  public void reading_elements_from_not_stored_file_array_fails() throws Exception {
    given(fileArray = objectsDb.read(FILE_ARRAY, HashCode.fromInt(33)));
    when(fileArray).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  @Test
  public void reading_elements_from_not_stored_blob_array_fails() throws Exception {
    given(blobArray = objectsDb.read(BLOB_ARRAY, HashCode.fromInt(33)));
    when(blobArray).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  @Test
  public void reading_elements_from_not_stored_string_array_fails() throws Exception {
    given(stringArray = objectsDb.read(STRING_ARRAY, HashCode.fromInt(33)));
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
    given(blob = objectsDb.read(BLOB, HashCode.fromInt(33)));
    when(blob).openInputStream();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  @Test
  public void reading_not_stored_sstring_fails() throws Exception {
    given(stringValue = objectsDb.read(STRING, HashCode.fromInt(33)));
    when(stringValue).value();
    thenThrown(NoObjectWithGivenHashError.class);
  }
}
