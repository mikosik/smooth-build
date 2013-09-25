package org.smoothbuild.task;

import org.smoothbuild.message.Error;
import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class DuplicatePathError extends Error {
  public DuplicatePathError(Path path) {
    super("Two Files with the same path = " + path + " cannot belong to the same File*");
  }
}
