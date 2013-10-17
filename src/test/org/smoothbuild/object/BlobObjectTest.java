package org.smoothbuild.object;

import static org.smoothbuild.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.TestFileSystem;

import com.google.common.hash.HashCode;

public class BlobObjectTest {
  String content = "content";
  TestFileSystem objectsFileSystem = new TestFileSystem();
  HashCode hash = HashCode.fromInt(1);
  BlobObject blobObject = new BlobObject(objectsFileSystem, hash);

  @Test
  public void hash() {
    given(blobObject = new BlobObject(objectsFileSystem, hash));
    when(blobObject.hash());
    thenReturned(hash);
  }

  @Test
  public void open_input_stream_reads_from_file_on_file_system_with_name_equal_to_blob_hash()
      throws IOException {
    given(objectsFileSystem).createFileWithContent(path(hash.toString()), content);
    when(StreamTester.inputStreamToString(blobObject.openInputStream()));
    thenReturned(content);
  }
}
