package org.smoothbuild.testing.common;

import static com.google.common.base.Charsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.common.io.CharStreams;

public class StreamTester {

  public static InputStream inputStreamWithContent(String content) throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    writeAndClose(stream, content);
    return new ByteArrayInputStream(stream.toByteArray());
  }

  public static void writeAndClose(OutputStream outputStream, String content) throws IOException {
    try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);) {
      writer.write(content);
    }
  }

  public static void assertContent(InputStream inputStream, String content) throws IOException,
      AssertionError {
    String actual = inputStreamToString(inputStream);
    if (!actual.equals(content)) {
      throw new AssertionError("File content is incorrect. Expected '" + content + "' but was '"
          + actual + "'.");
    }
  }

  public static String inputStreamToString(InputStream inputStream) throws IOException {
    try (InputStreamReader is = new InputStreamReader(inputStream, UTF_8)) {
      return CharStreams.toString(is);
    }
  }
}
