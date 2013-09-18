package org.smoothbuild.builtin.compress.err;

import org.smoothbuild.message.Error;
import org.smoothbuild.plugin.api.Path;

public class DuplicatePathInZipError extends Error {
  public DuplicatePathInZipError(Path path) {
    super("Zip file contains two files with the same path = " + path);
  }
}
