package org.smoothbuild.db.objects.marshal;

import static org.smoothbuild.SmoothContants.CHARSET;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.OutputStream;

import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.base.SBlob;

public class BlobWriterTest {
  HashedDb hashedDb = new HashedDb(new MemoryFileSystem());
  BlobWriter blobWriter;
  SBlob blob;
  OutputStream outputStream;
  String string = "my string";

  @Test
  public void opening_output_stream_twice_fails() throws Exception {
    given(blobWriter = new BlobWriter(hashedDb));
    given(blobWriter).openOutputStream();
    when(blobWriter).openOutputStream();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_fails_when_no_content_was_provided() {
    given(blobWriter = new BlobWriter(hashedDb));
    when(blobWriter).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_returns_blob_stored_in_object_db_with_empty_content() throws Exception {
    given(blobWriter = new BlobWriter(hashedDb));
    given(blobWriter.openOutputStream()).close();
    when(blob = blobWriter.build());
    thenEqual(inputStreamToString(blob.openInputStream()), "");
  }

  @Test
  public void build_returns_blob_stored_in_object_db() throws Exception {
    given(blobWriter = new BlobWriter(hashedDb));
    given(outputStream = blobWriter.openOutputStream());
    given(outputStream).write(string.getBytes(CHARSET));
    given(outputStream).close();
    when(blob = blobWriter.build());
    thenEqual(inputStreamToString(blob.openInputStream()), string);
  }
}
