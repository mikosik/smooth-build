package org.smoothbuild.util;

import static com.google.common.base.Charsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.common.io.CharStreams;

public class Streams {
  public static String inputStreamToString(InputStream inputStream) throws IOException {
    try (InputStreamReader is = new InputStreamReader(inputStream, UTF_8)) {
      return CharStreams.toString(is);
    }
  }
}
