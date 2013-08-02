package org.smoothbuild.fs.base;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * All methods of this interface require parameters which are paths that are in
 * valid canonical form as defined by {@link PathUtils} class. For performance
 * reasons implementations are not required to do any checking for validity or
 * canonicalization of path parameters and may return wrong results or behave
 * incorrectly in such cases.
 * 
 * All errors are reported by throwing {@link FileSystemException}.
 */
public interface FileSystem {
  public boolean pathExists(String path);

  /**
   * @return true when path exists and is a directory
   * @throws FileSystemException
   *           when path doesn't exist
   */
  public boolean isDirectory(String path);

  /**
   * @return list of childs of given directory
   */
  public List<String> childNames(String directory);

  public Iterable<String> filesFrom(String directory);

  public void copy(String sourceFile, String destinationFile);

  public InputStream createInputStream(String path);

  public OutputStream createOutputStream(String path);
}
