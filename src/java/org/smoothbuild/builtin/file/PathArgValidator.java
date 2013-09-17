package org.smoothbuild.builtin.file;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.plugin.api.Path.validationError;

import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.message.MessageListener;
import org.smoothbuild.plugin.api.Path;

public class PathArgValidator {
  public static Path validatedPath(String name, String value, MessageListener messages) {
    String message = validationError(value);
    if (message != null) {
      messages.report(new IllegalPathError(name, message));
      return null;
    }

    return path(value);
  }
}
