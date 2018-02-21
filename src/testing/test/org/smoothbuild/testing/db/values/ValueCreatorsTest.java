package org.smoothbuild.testing.db.values;

import static org.smoothbuild.testing.db.values.ValueCreators.blob;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.ValueFactory;

public class ValueCreatorsTest {
  private Blob blob;
  private final byte[] bytes = new byte[] { 1, 2, 3 };

  @Test
  public void creates_blob_with_bytes_as_content() throws Exception {
    given(blob = blob(new ValueFactory(), bytes));
    when(inputStreamToByteArray(blob.openInputStream()));
    thenReturned(bytes);
  }
}
