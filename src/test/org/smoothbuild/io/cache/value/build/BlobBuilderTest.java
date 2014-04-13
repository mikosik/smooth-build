package org.smoothbuild.io.cache.value.build;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.io.cache.value.ObjectsDb;
import org.smoothbuild.io.cache.value.instance.CachedBlob;

public class BlobBuilderTest {
  ObjectsDb objectsDb = mock(ObjectsDb.class);
  BlobBuilder blobBuilder = new BlobBuilder(objectsDb);
  byte[] bytes = new byte[] { 1, 2, 3 };
  CachedBlob blob = mock(CachedBlob.class);

  @Test
  public void opening_output_stream_twice_fails() throws Exception {
    given(blobBuilder).openOutputStream();
    when(blobBuilder).openOutputStream();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_fails_when_no_content_was_provided() {
    when(blobBuilder).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_returns_blob_stored_in_object_db_with_empty_content() throws Exception {
    given(willReturn(blob), objectsDb).writeBlob(new byte[] {});
    given(blobBuilder).openOutputStream();
    when(blobBuilder).build();
    thenReturned(blob);
  }

  @Test
  public void build_returns_blob_stored_in_object_db() throws Exception {
    given(willReturn(blob), objectsDb).writeBlob(bytes);
    given(blobBuilder.openOutputStream()).write(bytes);
    when(blobBuilder).build();
    thenReturned(blob);
  }
}
