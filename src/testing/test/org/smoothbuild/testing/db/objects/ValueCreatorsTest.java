package org.smoothbuild.testing.db.objects;

import static org.smoothbuild.db.objects.ObjectsDb.objectsDb;
import static org.smoothbuild.testing.db.objects.ValueCreators.blob;
import static org.smoothbuild.testing.db.objects.ValueCreators.file;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Blob;

public class ValueCreatorsTest {
  private final String string = "my string";
  private final Path path = Path.path("my/file");
  private final ObjectsDb objectsDb = objectsDb();
  private Blob blob;
  private final byte[] bytes = new byte[] { 1, 2, 3 };

  @Test
  public void creates_file_with_path_as_content() throws Exception {
    given(blob = blob(objectsDb, path.value()));
    when(file(objectsDb, path));
    thenReturned(objectsDb.file(path, blob));
  }

  @Test
  public void creates_file_with_string_as_content() throws Exception {
    given(blob = blob(objectsDb, string));
    when(file(objectsDb, path, string));
    thenReturned(objectsDb.file(path, blob));
  }

  @Test
  public void creates_file_with_bytes_as_content() throws Exception {
    given(blob = blob(objectsDb, bytes));
    when(file(objectsDb, path, bytes));
    thenReturned(objectsDb.file(path, blob));
  }

  @Test
  public void creates_blob_with_string_as_content() throws Exception {
    given(blob = blob(objectsDb, string));
    when(inputStreamToString(blob.openInputStream()));
    thenReturned(string);
  }

  @Test
  public void creates_blob_with_bytes_as_content() throws Exception {
    given(blob = blob(objectsDb, bytes));
    when(inputStreamToByteArray(blob.openInputStream()));
    thenReturned(bytes);
  }
}
