package org.smoothbuild.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;

import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.SString;

public class PathArgValidator {
  private static final String PROJECT_ROOT = "//";

  public static Path validatedProjectPath(String name, SString stringValue) {
    String value = stringValue.value();
    if (!value.startsWith(PROJECT_ROOT)) {
      throw new IllegalPathError(name, "It should start with \"" + PROJECT_ROOT
          + "\" which represents project's root dir.");
    }
    return validatedPath(name, stringValue.value().substring(PROJECT_ROOT.length()));
  }

  public static Path validatedPath(String name, SString stringValue) {
    return validatedPath(name, stringValue.value());
  }

  private static Path validatedPath(String name, String value) {
    String message = validationError(value);
    if (message != null) {
      throw new IllegalPathError(name, message);
    }
    return path(value);
  }
}
