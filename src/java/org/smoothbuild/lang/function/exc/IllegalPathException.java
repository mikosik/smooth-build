package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.function.Param;

@SuppressWarnings("serial")
public class IllegalPathException extends IllegalArgException {
  public IllegalPathException(Param<String> path, String message) {
    super(path, message);
  }
}
