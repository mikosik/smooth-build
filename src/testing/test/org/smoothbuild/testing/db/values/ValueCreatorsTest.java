package org.smoothbuild.testing.db.values;

import static org.smoothbuild.testing.db.values.ValueCreators.blob;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.TestingValueFactory;

import okio.ByteString;

public class ValueCreatorsTest {
  private Blob blob;
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  public void creates_blob_with_bytes_as_content() throws Exception {
    given(blob = blob(new TestingValueFactory(), bytes));
    when(blob.source().readByteString());
    thenReturned(bytes);
  }
}
