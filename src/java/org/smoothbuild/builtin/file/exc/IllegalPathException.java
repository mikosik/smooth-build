package org.smoothbuild.builtin.file.exc;

import org.smoothbuild.plugin.exc.IllegalArgException;


@SuppressWarnings("serial")
public class IllegalPathException extends IllegalArgException {
  public IllegalPathException(String paramName, String message) {
    super(paramName, message);
  }
}
