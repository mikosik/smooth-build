package org.smoothbuild.builtin.file.exc;

import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.IllegalArgException;

@SuppressWarnings("serial")
public class PathIsNotAFileException extends IllegalArgException {
  public PathIsNotAFileException(String paramName, Path path) {
    super(paramName, "Path " + path + " exists but is not a file but a directory.");
  }
}
