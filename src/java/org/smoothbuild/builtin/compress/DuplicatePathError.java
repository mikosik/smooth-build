package org.smoothbuild.builtin.compress;

import org.smoothbuild.message.Error;
import org.smoothbuild.plugin.api.Path;

public class DuplicatePathError extends Error {
  public DuplicatePathError(Path path) {
    super("Zip file contains two files with the same path = " + path);
  }
}
