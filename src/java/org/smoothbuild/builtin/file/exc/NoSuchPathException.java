package org.smoothbuild.builtin.file.exc;

import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.IllegalArgException;

@SuppressWarnings("serial")
public class NoSuchPathException extends IllegalArgException {
  public NoSuchPathException(String paramName, Path part) {
    super(paramName, "Path " + part + " doesn't exist.");
  }
}
