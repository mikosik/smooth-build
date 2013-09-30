package org.smoothbuild.task.err;

import org.smoothbuild.message.message.Error;
import org.smoothbuild.plugin.api.Path;

public class DuplicatePathError extends Error {
  public DuplicatePathError(Path path) {
    super("Two Files with the same path = " + path + " cannot belong to the same File*");
  }
}
