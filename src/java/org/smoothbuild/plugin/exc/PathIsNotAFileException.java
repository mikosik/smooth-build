package org.smoothbuild.plugin.exc;

import org.smoothbuild.plugin.Path;

@SuppressWarnings("serial")
public class PathIsNotAFileException extends IllegalArgException {
  public PathIsNotAFileException(String paramName, Path path) {
    super(paramName, "Path " + path + " exists but is not a file but a directory.");
  }
}
