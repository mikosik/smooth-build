package org.smoothbuild.testing.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StreamTester {

  public static Void writeAndClose(OutputStream outputStream, String content) throws IOException {
    try (OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
      writer.write(content);
    }
    return null;
  }
}
