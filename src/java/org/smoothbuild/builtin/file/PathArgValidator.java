package org.smoothbuild.builtin.file;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.plugin.api.Path.validationError;

import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.PluginErrorException;

public class PathArgValidator {
  public static Path validatedPath(String name, String value) {
    String message = validationError(value);
    if (message != null) {
      throw new PluginErrorException(new IllegalPathError(name, message));
    }

    return path(value);
  }
}
