package org.smoothbuild.common.bucket.base;

import java.io.IOException;

public interface PathIterator {
  public boolean hasNext();

  public Path next() throws IOException;
}
