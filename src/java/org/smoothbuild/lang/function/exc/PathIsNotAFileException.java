package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.type.Path;

@SuppressWarnings("serial")
public class PathIsNotAFileException extends IllegalArgException {
  public PathIsNotAFileException(String paramName, Path path) {
    super(paramName, "Path " + path + " exists but is not a file but a directory.");
  }
}
