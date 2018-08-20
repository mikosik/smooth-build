package org.smoothbuild.io.fs.base;

import java.io.InputStream;
import java.io.OutputStream;

import okio.BufferedSink;
import okio.BufferedSource;

/**
 * All errors are reported by throwing {@link FileSystemException}.
 */
public interface FileSystem {
  public PathState pathState(Path path);

  public Iterable<Path> files(Path dir);

  public void move(Path source, Path target);

  public void delete(Path path);

  public InputStream openInputStream(Path path);

  public OutputStream openOutputStream(Path path);

  public BufferedSource source(Path path);

  public BufferedSink sink(Path path);

  public void createLink(Path link, Path target);

  public void createDir(Path path);
}
