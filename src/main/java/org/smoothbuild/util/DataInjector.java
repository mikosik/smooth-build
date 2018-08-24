package org.smoothbuild.util;

import java.io.IOException;

import okio.BufferedSink;

@FunctionalInterface
public interface DataInjector {
  public void injectTo(BufferedSink sink) throws IOException;
}