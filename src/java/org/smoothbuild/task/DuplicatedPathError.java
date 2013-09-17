package org.smoothbuild.task;

import org.smoothbuild.message.Error;
import org.smoothbuild.plugin.api.Path;

public class DuplicatedPathError extends Error {
  public DuplicatedPathError(Path path) {
    super("Two Files with the same path = " + path + " cannot belong to the same File*");
  }
}
