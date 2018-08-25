package org.smoothbuild.util;

import java.io.IOException;

import okio.BufferedSource;
import okio.Sink;

public class Okios {
  public static void copyAllAndClose(BufferedSource source, Sink sink) throws IOException {
    try (BufferedSource toClose1 = source; Sink toClose2 = sink) {
      source.readAll(sink);
    }
  }
}
