package org.smoothbuild.lang.builtin.file.err;

import static org.smoothbuild.io.fs.FileSystemModule.SMOOTH_DIR;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

public class CannotListRootDirError extends Message {
  public CannotListRootDirError() {
    super(ERROR, "Cannot take all files from root dir '.' as it would also include smooth dir "
        + SMOOTH_DIR + " which is forbidden.");
  }
}
