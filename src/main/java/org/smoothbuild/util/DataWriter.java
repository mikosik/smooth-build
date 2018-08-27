package org.smoothbuild.util;

import java.io.IOException;

import okio.BufferedSink;

@FunctionalInterface
public interface DataWriter {
  public void writeTo(BufferedSink sink) throws IOException;
}