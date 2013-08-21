package org.smoothbuild.plugin.exc;

import org.smoothbuild.plugin.Path;

@SuppressWarnings("serial")
public class NoSuchPathException extends IllegalArgException {
  public NoSuchPathException(String paramName, Path part) {
    super(paramName, "Path " + part + " doesn't exist.");
  }
}
