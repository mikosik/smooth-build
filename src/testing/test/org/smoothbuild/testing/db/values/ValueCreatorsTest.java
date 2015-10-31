package org.smoothbuild.testing.db.values;

import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.smoothbuild.testing.db.values.ValueCreators.blob;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Blob;

public class ValueCreatorsTest {
  private final String string = "my string";
  private final Path path = Path.path("my/file");
  private final ValuesDb valuesDb = valuesDb();
  private Blob blob;
  private final byte[] bytes = new byte[] { 1, 2, 3 };

  @Test
  public void creates_file_with_path_as_content() throws Exception {
    given(blob = blob(valuesDb, path.value()));
    when(file(valuesDb, path));
    thenReturned(valuesDb.file(path, blob));
  }

  @Test
  public void creates_file_with_string_as_content() throws Exception {
    given(blob = blob(valuesDb, string));
    when(file(valuesDb, path, string));
    thenReturned(valuesDb.file(path, blob));
  }

  @Test
  public void creates_file_with_bytes_as_content() throws Exception {
    given(blob = blob(valuesDb, bytes));
    when(file(valuesDb, path, bytes));
    thenReturned(valuesDb.file(path, blob));
  }

  @Test
  public void creates_blob_with_string_as_content() throws Exception {
    given(blob = blob(valuesDb, string));
    when(inputStreamToString(blob.openInputStream()));
    thenReturned(string);
  }

  @Test
  public void creates_blob_with_bytes_as_content() throws Exception {
    given(blob = blob(valuesDb, bytes));
    when(inputStreamToByteArray(blob.openInputStream()));
    thenReturned(bytes);
  }
}
