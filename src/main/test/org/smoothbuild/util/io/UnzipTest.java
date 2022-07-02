package org.smoothbuild.util.io;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.smoothbuild.util.io.Unzip.unzip;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.cnst.BlobB;
import org.smoothbuild.testing.TestContext;

import okio.Buffer;

public class UnzipTest extends TestContext {
  @Test
  public void files_are_unzipped() throws Exception {
    var files = Map.of("file1", "content1", "file2", "content2");
    var jarBlob = blobB(jarFiles(files).readByteString());
    assertThat(unzipIntoMap(jarBlob))
        .isEqualTo(files);
  }

  private static Map<String, String> unzipIntoMap(BlobB jarBlob)
      throws IOException, IllegalZipEntryFileNameExc, DuplicateFileNameExc {
    Map<String, String> result = new HashMap<>();
    unzip(jarBlob, s -> true, (s, is) -> result.put(s, new String(is.readAllBytes(), UTF_8)));
    return result;
  }

  private static Buffer jarFiles(Map<String, String> files) throws IOException {
    Buffer buffer = new Buffer();
    try (var jarOutputStream = new JarOutputStream(buffer.outputStream())) {
      for (var entry : (Collection<? extends Entry<String, String>>) files.entrySet()) {
        jarOutputStream.putNextEntry(new ZipEntry(entry.getKey()));
        jarOutputStream.write(entry.getValue().getBytes(UTF_8));
      }
    }
    return buffer;
  }
}
