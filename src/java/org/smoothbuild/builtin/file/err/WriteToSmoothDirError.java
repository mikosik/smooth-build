package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.fs.FileSystemModule.SMOOTH_DIR;
import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;

public class WriteToSmoothDirError extends Message {
  public WriteToSmoothDirError(Path path) {
    super(ERROR, "Writing to " + SMOOTH_DIR + " dir is forbidden.\n"
        + "Smooth keeps internal data there so don't mess with it.\n" + "Faulty path = " + path);
  }
}
