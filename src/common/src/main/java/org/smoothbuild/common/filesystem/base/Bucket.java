package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.Sink;
import okio.Source;

/**
 * Container for files that are identified by {@link Path}.
 */
public interface Bucket {
  public PathState pathState(Path path) throws IOException;

  public Iterable<Path> files(Path dir) throws IOException;

  public void move(Path source, Path target) throws IOException;

  public void delete(Path path) throws IOException;

  public long size(Path path) throws IOException;

  public Source source(Path path) throws IOException;

  public Sink sink(Path path) throws IOException;

  public void createLink(Path link, Path target) throws IOException;

  public void createDir(Path path) throws IOException;
}
