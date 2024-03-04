package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;

public interface FileSystem {
  public PathState pathState(Path path);

  public Iterable<Path> files(Path dir) throws IOException;

  public void move(Path source, Path target) throws IOException;

  public void delete(Path path) throws IOException;

  public long size(Path path) throws IOException;

  public BufferedSource source(Path path) throws IOException;

  public BufferedSink sink(Path path) throws IOException;

  public Sink sinkWithoutBuffer(Path path) throws IOException;

  public void createLink(Path link, Path target) throws IOException;

  public void createDir(Path path) throws IOException;
}
