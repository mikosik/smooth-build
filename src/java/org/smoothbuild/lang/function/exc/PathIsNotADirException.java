package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.type.Path;

@SuppressWarnings("serial")
public class PathIsNotADirException extends IllegalArgException {
  public PathIsNotADirException(String paramName, Path path) {
    super(paramName, "Path " + path + " exists but is not a directory but a file.");
  }
}
