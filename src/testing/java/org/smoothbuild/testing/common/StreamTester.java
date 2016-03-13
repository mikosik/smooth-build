package org.smoothbuild.testing.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StreamTester {

  public static InputStream inputStreamContaining(String content) {
    return new ByteArrayInputStream(content.getBytes());
  }

  public static Void writeAndClose(OutputStream outputStream, String content) throws IOException {
    try (OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
      writer.write(content);
    }
    return null;
  }
}
