package org.smoothbuild.fs.base;

import java.io.IOException;
import java.nio.file.Path;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;

public interface FileSystem {
  public Path rootDirJPath();

  public PathState pathState(PathS path);

  public Iterable<PathS> files(PathS dir) throws IOException;

  public void move(PathS source, PathS target) throws IOException;

  public void delete(PathS path) throws IOException;

  public long size(PathS path) throws IOException;

  public BufferedSource source(PathS path) throws IOException;

  public BufferedSink sink(PathS path) throws IOException;

  public Sink sinkWithoutBuffer(PathS path) throws IOException;

  public void createLink(PathS link, PathS target) throws IOException;

  public void createDir(PathS path) throws IOException;
}
