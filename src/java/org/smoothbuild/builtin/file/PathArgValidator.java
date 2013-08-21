package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.function.exc.IllegalPathException;
import org.smoothbuild.lang.function.exc.MissingArgException;
import org.smoothbuild.lang.type.Path;

public class PathArgValidator {
  public static Path validatedPath(String name, String value) throws MissingArgException,
      IllegalPathException {
    if (value == null) {
      throw new MissingArgException(name);
    }
    String message = Path.validationError(value);
    if (message != null) {
      throw new IllegalPathException(name, message);
    }

    return Path.path(value);
  }
}
