package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.type.Path;

@SuppressWarnings("serial")
public class NoSuchPathException extends IllegalArgException {
  public NoSuchPathException(Param<?> param, Path part) {
    super(param, "Path " + part + " doesn't exist.");
  }
}
