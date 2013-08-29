package org.smoothbuild.testing;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.common.io.LineReader;

public class TestingStream {

  public static void writeAndClose(OutputStream outputStream, String content) throws IOException {
    try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);) {
      writer.write(content);
    }
  }

  public static void assertContent(InputStream inputStream, String content) throws IOException,
      AssertionError {
    try (InputStreamReader readable = new InputStreamReader(inputStream);) {
      LineReader reader = new LineReader(readable);
      String actual = reader.readLine();
      if (!actual.equals(content)) {
        throw new AssertionError("File content is incorrect. Expected '" + content + "' but was '"
            + actual + "'.");
      }
      if (reader.readLine() != null) {
        throw new AssertionError("File has more than one line.");
      }
    }
  }
}
