package org.smoothbuild.object.err;

import org.smoothbuild.fs.base.Path;

public class DuplicatePathError extends ObjectDbError {
  public DuplicatePathError(Path path) {
    super("Two Files with the same path = " + path + " cannot belong to the same File*");
  }
}
