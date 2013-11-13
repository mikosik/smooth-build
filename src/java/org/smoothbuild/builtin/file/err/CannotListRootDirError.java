package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.fs.FileSystemModule.SMOOTH_DIR;
import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class CannotListRootDirError extends Message {
  public CannotListRootDirError() {
    super(ERROR, "Cannot take all files from root dir '.' as it would also include smooth dir "
        + SMOOTH_DIR + " which is forbidden.");
  }
}
