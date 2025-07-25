package org.smoothbuild.common.io;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.smoothbuild.common.io.Unzip.unzip;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import okio.Buffer;
import okio.BufferedSource;
import org.junit.jupiter.api.Test;

public class UnzipTest {
  @Test
  void files_are_unzipped() throws Exception {
    var files = Map.of("file1", "content1", "file2", "content2");
    Buffer buffer = jarFiles(files);
    assertThat(unzipIntoMap(buffer)).isEqualTo(files);
  }

  private static Map<String, String> unzipIntoMap(BufferedSource source) throws Exception {
    Map<String, String> result = new HashMap<>();
    unzip(source, s -> true, (s1, is) -> result.put(s1, new String(is.readAllBytes(), UTF_8)));
    return result;
  }

  private static Buffer jarFiles(Map<String, String> files) throws IOException {
    Buffer buffer = new Buffer();
    try (var jarOutputStream = new JarOutputStream(buffer.outputStream())) {
      for (var entry : files.entrySet()) {
        jarOutputStream.putNextEntry(new ZipEntry(entry.getKey()));
        jarOutputStream.write(entry.getValue().getBytes(UTF_8));
      }
    }
    return buffer;
  }
}
