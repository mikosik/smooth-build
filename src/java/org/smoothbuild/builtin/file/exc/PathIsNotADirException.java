package org.smoothbuild.builtin.file.exc;

import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.IllegalArgException;

@SuppressWarnings("serial")
public class PathIsNotADirException extends IllegalArgException {
  public PathIsNotADirException(String paramName, Path path) {
    super(paramName, "Path " + path + " exists but is not a directory but a file.");
  }
}
