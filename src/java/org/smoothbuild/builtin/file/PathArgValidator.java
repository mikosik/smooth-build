package org.smoothbuild.builtin.file;

import static org.smoothbuild.type.api.Path.path;
import static org.smoothbuild.type.api.Path.validationError;

import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.type.api.Path;

public class PathArgValidator {
  public static Path validatedPath(String name, String value) {
    String message = validationError(value);
    if (message != null) {
      throw new ErrorMessageException(new IllegalPathError(name, message));
    }

    return path(value);
  }
}
