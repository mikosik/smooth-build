package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class CannotListRootDirError extends Message {
  public CannotListRootDirError() {
    super(ERROR, "Cannot take all files from root dir '.' as it would also include smooth dir "
        + BUILD_DIR + " which is forbidden.");
  }
}
