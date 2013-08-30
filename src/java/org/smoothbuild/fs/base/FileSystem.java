package org.smoothbuild.fs.base;

import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.Path;

import com.google.inject.ImplementedBy;

/**
 * All errors are reported by throwing {@link FileSystemException}.
 */
@ImplementedBy(DiskFileSystem.class)
public interface FileSystem {
  public boolean pathExists(Path path);

  public boolean pathExistsAndisDirectory(Path path);

  public Iterable<String> childNames(Path directory);

  public Iterable<Path> filesFrom(Path directory);

  public void copy(Path sourceFile, Path destinationFile);

  public InputStream createInputStream(Path path);

  public OutputStream createOutputStream(Path path);
}
