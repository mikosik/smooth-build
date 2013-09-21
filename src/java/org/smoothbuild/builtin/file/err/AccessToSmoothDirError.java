package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;

import org.smoothbuild.message.Error;

public class AccessToSmoothDirError extends Error {
  public AccessToSmoothDirError() {
    super("Accessing " + BUILD_DIR
        + " is forbidden. Smooth keeps internal data there so don't mess with it.");
  }
}
