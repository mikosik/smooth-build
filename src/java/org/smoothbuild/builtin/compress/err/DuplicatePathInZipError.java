package org.smoothbuild.builtin.compress.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class DuplicatePathInZipError extends ErrorMessage {
  public DuplicatePathInZipError(Path path) {
    super("Zip file contains two files with the same path = " + path);
  }
}
