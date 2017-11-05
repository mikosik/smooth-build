package org.smoothbuild.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.io.fs.base.IllegalPathException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.value.SString;

public class PathArgValidator {
  private static final String PROJECT_ROOT = "//";

  public static Path validatedProjectPath(String name, SString stringValue) {
    String value = stringValue.value();
    if (!value.startsWith(PROJECT_ROOT)) {
      throw new ErrorMessage("Param '" + name + "' has illegal value. It should start with \""
          + PROJECT_ROOT + "\" which represents project's root dir.");
    }
    return validatedPath(name, stringValue.value().substring(PROJECT_ROOT.length()));
  }

  private static Path validatedPath(String name, String value) {
    try {
      return path(value);
    } catch (IllegalPathException e) {
      throw new ErrorMessage("Param '" + name + "' has illegal value. " + e.getMessage());
    }
  }
}
