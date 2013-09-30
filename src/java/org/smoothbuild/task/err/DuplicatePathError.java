package org.smoothbuild.task.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class DuplicatePathError extends ErrorMessage {
  public DuplicatePathError(Path path) {
    super("Two Files with the same path = " + path + " cannot belong to the same File*");
  }
}
