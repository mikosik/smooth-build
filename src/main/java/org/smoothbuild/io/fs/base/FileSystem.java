package org.smoothbuild.io.fs.base;

import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.io.fs.base.err.FileSystemError;

/**
 * All errors are reported by throwing {@link FileSystemError}.
 */
public interface FileSystem {
  public PathState pathState(Path path);

  public Iterable<Path> childNames(Path directory);

  public Iterable<Path> filesFrom(Path directory);

  public void delete(Path path);

  public InputStream openInputStream(Path path);

  public OutputStream openOutputStream(Path path);

  public void createLink(Path link, Path target);

  public void createDir(Path path);
}
