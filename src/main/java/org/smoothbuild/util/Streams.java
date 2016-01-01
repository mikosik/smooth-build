package org.smoothbuild.util;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

/**
 * Utility methods for converting stream data to required format and closing
 * stream afterwards.
 */
public class Streams {
  public static String inputStreamToString(InputStream inputStream) throws IOException {
    try (InputStreamReader is = new InputStreamReader(inputStream, CHARSET)) {
      return CharStreams.toString(is);
    }
  }

  public static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
    try (InputStream is = inputStream) {
      return ByteStreams.toByteArray(is);
    }
  }

  public static void copy(InputStream from, OutputStream to) throws IOException {
    try (InputStream input = from; OutputStream output = to) {
      ByteStreams.copy(input, output);
    }
  }
}
