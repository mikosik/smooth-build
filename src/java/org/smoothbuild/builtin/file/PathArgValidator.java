package org.smoothbuild.builtin.file;

import org.smoothbuild.builtin.file.exc.IllegalPathException;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.MissingArgException;

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
