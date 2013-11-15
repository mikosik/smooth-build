package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.file.err.IllegalPathError;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.message.listen.ErrorMessageException;

public class PathArgValidator {
  public static Path validatedPath(String name, StringValue stringValue) {
    String value = stringValue.value();
    String message = validationError(value);
    if (message != null) {
      throw new ErrorMessageException(new IllegalPathError(name, message));
    }

    return path(value);
  }
}
