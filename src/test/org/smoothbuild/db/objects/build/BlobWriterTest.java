package org.smoothbuild.db.objects.build;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.db.objects.instance.BlobObject;

public class BlobWriterTest {
  ObjectsDb objectsDb = mock(ObjectsDb.class);
  BlobWriter blobWriter = new BlobWriter(objectsDb);
  byte[] bytes = new byte[] { 1, 2, 3 };
  BlobObject blob = mock(BlobObject.class);

  @Test
  public void opening_output_stream_twice_fails() throws Exception {
    given(blobWriter).openOutputStream();
    when(blobWriter).openOutputStream();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_fails_when_no_content_was_provided() {
    when(blobWriter).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_returns_blob_stored_in_object_db_with_empty_content() throws Exception {
    given(willReturn(blob), objectsDb).writeBlob(new byte[] {});
    given(blobWriter).openOutputStream();
    when(blobWriter).build();
    thenReturned(blob);
  }

  @Test
  public void build_returns_blob_stored_in_object_db() throws Exception {
    given(willReturn(blob), objectsDb).writeBlob(bytes);
    given(blobWriter.openOutputStream()).write(bytes);
    when(blobWriter).build();
    thenReturned(blob);
  }
}
