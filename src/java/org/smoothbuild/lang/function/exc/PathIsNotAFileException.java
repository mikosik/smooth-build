package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.type.Path;

@SuppressWarnings("serial")
public class PathIsNotAFileException extends IllegalArgException {
  public PathIsNotAFileException(Param<?> param, Path path) {
    super(param, "Path " + path + " exists but is not a file but a directory.");
  }
}
