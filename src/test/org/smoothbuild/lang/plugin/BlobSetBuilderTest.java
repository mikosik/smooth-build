package org.smoothbuild.lang.plugin;

import static org.hamcrest.Matchers.emptyIterable;
import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;

public class BlobSetBuilderTest {
  String content = "content";
  FakeValueDb objectDb = new FakeValueDb();
  Blob blob;

  BlobSetBuilder blobSetBuilder = new BlobSetBuilder(objectDb);

  @Test
  public void build_returns_empty_blob_set_when_no_blob_has_been_added() throws IOException {
    when(blobSetBuilder.build());
    thenReturned(emptyIterable());
  }

  @Test
  public void build_returns_set_containing_added_blob() throws Exception {
    given(blob = objectDb.blob(content.getBytes(CHARSET)));
    given(blobSetBuilder).add(blob);
    when(inputStreamToString(blobSetBuilder.build().iterator().next().openInputStream()));
    thenReturned(content);
  }
}
