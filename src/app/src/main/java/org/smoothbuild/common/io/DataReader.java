package org.smoothbuild.common.io;

import java.io.IOException;
import okio.BufferedSource;

@FunctionalInterface
public interface DataReader<T> {
  public T readFrom(BufferedSource source) throws IOException;
}
