package org.smoothbuild.testing.common;

import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.common.io.ByteStreams;

public class StreamTester {

  public static InputStream inputStreamContaining(String content) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    try {
      writeAndClose(stream, content);
    } catch (IOException e) {
      throw new RuntimeException("IOException should never be thrown by ByteArrayOutputStream.", e);
    }
    return new ByteArrayInputStream(stream.toByteArray());
  }

  public static Void writeAndClose(OutputStream outputStream, String content) throws IOException {
    try (OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
      writer.write(content);
    }
    return null;
  }

  public static void assertContent(InputStream inputStream, String content) throws IOException,
      AssertionError {
    String actual = inputStreamToString(inputStream);
    if (!actual.equals(content)) {
      throw new AssertionError("File content is incorrect. Expected '" + content + "' but was '"
          + actual + "'.");
    }
  }

  public static byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
    try {
      return ByteStreams.toByteArray(inputStream);
    } finally {
      inputStream.close();
    }
  }
}
