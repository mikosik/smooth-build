package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.type.Path;

@SuppressWarnings("serial")
public class NoSuchPathException extends IllegalArgException {
  public NoSuchPathException(String paramName, Path part) {
    super(paramName, "Path " + part + " doesn't exist.");
  }
}
