package org.smoothbuild.fs.base;

import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.exc.FileSystemException;

import com.google.inject.ImplementedBy;

/**
 * All errors are reported by throwing {@link FileSystemException}.
 */
@ImplementedBy(DiskFileSystem.class)
public interface FileSystem {
  public Path root();

  public PathKind pathKind(Path path);

  public Iterable<String> childNames(Path directory);

  public Iterable<Path> filesFrom(Path directory);

  public void copy(Path sourceFile, Path destinationFile);

  public void deleteDirectoryRecursively(Path directory);

  public InputStream openInputStream(Path path);

  public OutputStream openOutputStream(Path path);
}
