package org.smoothbuild.fs.base;

import java.io.IOException;

public interface PathIterator {
  public boolean hasNext();

  public PathS next() throws IOException;
}
