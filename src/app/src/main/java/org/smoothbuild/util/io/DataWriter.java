package org.smoothbuild.util.io;

import java.io.IOException;

import okio.BufferedSink;

@FunctionalInterface
public interface DataWriter {
  public void writeTo(BufferedSink sink) throws IOException;
}
