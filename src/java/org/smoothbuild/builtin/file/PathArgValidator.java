package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.exc.IllegalPathException;
import org.smoothbuild.lang.function.exc.MissingArgException;
import org.smoothbuild.lang.type.Path;

public class PathArgValidator {
  public static Path validatedPath(Param<String> path) throws MissingArgException,
      IllegalPathException {
    if (!path.isSet()) {
      throw new MissingArgException(path);
    }
    String message = Path.validationError(path.get());
    if (message != null) {
      throw new IllegalPathException(path, message);
    }

    return Path.path(path.get());
  }
}
