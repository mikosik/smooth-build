package org.smoothbuild.testing.db.values;

import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.db.values.ValueCreators.blob;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.smoothbuild.testing.db.values.ValueCreators.string;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Blob;

public class ValueCreatorsTest {
  private final String content = "my string";
  private final String path = "my/file";
  private final ValuesDb valuesDb = memoryValuesDb();
  private Blob blob;
  private final byte[] bytes = new byte[] { 1, 2, 3 };

  @Test
  public void creates_file_with_path_as_content() throws Exception {
    when(file(valuesDb, path(path)));
    thenReturned(valuesDb.file(string(valuesDb, path), blob(valuesDb, path)));
  }

  @Test
  public void creates_file_with_string_as_content() throws Exception {
    when(file(valuesDb, path(path), content));
    thenReturned(valuesDb.file(string(valuesDb, path), blob(valuesDb, content)));
  }

  @Test
  public void creates_file_with_bytes_as_content() throws Exception {
    when(file(valuesDb, path(path), bytes));
    thenReturned(valuesDb.file(string(valuesDb, path), blob(valuesDb, bytes)));
  }

  @Test
  public void creates_blob_with_string_as_content() throws Exception {
    given(blob = blob(valuesDb, content));
    when(inputStreamToString(blob.openInputStream()));
    thenReturned(content);
  }

  @Test
  public void creates_blob_with_bytes_as_content() throws Exception {
    given(blob = blob(valuesDb, bytes));
    when(inputStreamToByteArray(blob.openInputStream()));
    thenReturned(bytes);
  }
}
