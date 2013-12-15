package org.smoothbuild.util;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.smoothbuild.io.fs.base.exc.FileSystemException;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

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

  public static void copy(InputStream from, OutputStream to) {
    try (InputStream input = from; OutputStream output = to) {
      ByteStreams.copy(input, output);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
