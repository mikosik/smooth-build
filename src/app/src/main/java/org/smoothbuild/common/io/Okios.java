package org.smoothbuild.common.io;

import java.io.IOException;
import java.math.BigInteger;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
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

  public static void writeAndClose(BufferedSink sink, DataWriter writer) throws IOException {
    try (sink) {
      writer.writeTo(sink);
    }
  }

  public static ByteString intToByteString(int data) {
    return ByteString.of(BigInteger.valueOf(data).toByteArray());
  }
}
