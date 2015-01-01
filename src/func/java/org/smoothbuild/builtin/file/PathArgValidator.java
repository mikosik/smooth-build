package org.smoothbuild.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;

import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.SString;

public class PathArgValidator {
  public static Path validatedPath(String name, SString stringValue) {
    String value = stringValue.value();
    String message = validationError(value);
    if (message != null) {
      throw new IllegalPathError(name, message);
    }

    return path(value);
  }
}
