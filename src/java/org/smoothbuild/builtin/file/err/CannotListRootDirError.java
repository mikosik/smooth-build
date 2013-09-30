package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;

import org.smoothbuild.message.message.ErrorMessage;

public class CannotListRootDirError extends ErrorMessage {
  public CannotListRootDirError() {
    super("Cannot take all files from root dir '.' as it would also include smooth dir "
        + BUILD_DIR + " which is forbidden.");
  }
}
