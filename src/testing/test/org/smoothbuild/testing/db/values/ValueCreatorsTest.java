package org.smoothbuild.testing.db.values;

import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.testing.db.values.ValueCreators.blob;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Blob;

public class ValueCreatorsTest {
  private final ValuesDb valuesDb = memoryValuesDb();
  private Blob blob;
  private final byte[] bytes = new byte[] { 1, 2, 3 };

  @Test
  public void creates_blob_with_bytes_as_content() throws Exception {
    given(blob = blob(valuesDb, bytes));
    when(inputStreamToByteArray(blob.openInputStream()));
    thenReturned(bytes);
  }
}
