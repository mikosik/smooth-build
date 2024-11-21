package org.smoothbuild.common.filesystem.base;

import java.io.IOException;

public interface PathIterator {
  public boolean hasNext() throws IOException;

  public Path next() throws IOException;
}
