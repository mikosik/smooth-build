package org.smoothbuild.testing.io.cache.value;

import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class FakeObjectsDbTest {
  private final String string = "my string";
  private final Path path = Path.path("my/file");
  private final FakeObjectsDb fakeObjectsDb = new FakeObjectsDb();
  private SBlob blob;
  private final byte[] bytes = new byte[] { 1, 2, 3 };

  @Test
  public void creates_file_with_path_as_content() throws Exception {
    given(blob = fakeObjectsDb.blob(path.value()));
    when(fakeObjectsDb.file(path));
    thenReturned(fakeObjectsDb.file(path, blob));
  }

  @Test
  public void creates_file_with_string_as_content() throws Exception {
    given(blob = fakeObjectsDb.blob(string));
    when(fakeObjectsDb.file(path, string));
    thenReturned(fakeObjectsDb.file(path, blob));
  }

  @Test
  public void creates_file_with_bytes_as_content() throws Exception {
    given(blob = fakeObjectsDb.blob(bytes));
    when(fakeObjectsDb.file(path, bytes));
    thenReturned(fakeObjectsDb.file(path, blob));
  }

  @Test
  public void creates_blob_with_string_as_content() throws Exception {
    given(blob = fakeObjectsDb.blob(string));
    when(inputStreamToString(blob.openInputStream()));
    thenReturned(string);
  }

  @Test
  public void creates_blob_with_bytes_as_content() throws Exception {
    given(blob = fakeObjectsDb.blob(bytes));
    when(inputStreamToByteArray(blob.openInputStream()));
    thenReturned(bytes);
  }
}
