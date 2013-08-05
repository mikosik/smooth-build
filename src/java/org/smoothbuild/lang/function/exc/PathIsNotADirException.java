package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.type.Path;

@SuppressWarnings("serial")
public class PathIsNotADirException extends IllegalArgException {
  public PathIsNotADirException(Param<?> param, Path path) {
    super(param, "Path " + path + " exists but is not a directory but a file.");
  }
}
