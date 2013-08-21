package org.smoothbuild.plugin.exc;

import org.smoothbuild.plugin.Path;

@SuppressWarnings("serial")
public class PathIsNotADirException extends IllegalArgException {
  public PathIsNotADirException(String paramName, Path path) {
    super(paramName, "Path " + path + " exists but is not a directory but a file.");
  }
}
