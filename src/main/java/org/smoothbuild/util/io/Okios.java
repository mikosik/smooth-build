package org.smoothbuild.util.io;

import java.io.IOException;

import okio.BufferedSource;
import okio.Sink;

public class Okios {
  public static void copyAllAndClose(BufferedSource source, Sink sink) throws IOException {
    try (source; sink) {
      source.readAll(sink);
    }
  }

  public static <T> T readAndClose(BufferedSource source, DataReader<T> reader) throws IOException {
    try (source) {
      return reader.readFrom(source);
    }
  }
}
