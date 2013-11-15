package org.smoothbuild.lang.builtin.file.err;

import static org.smoothbuild.io.fs.FileSystemModule.SMOOTH_DIR;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class WriteToSmoothDirError extends Message {
  public WriteToSmoothDirError(Path path) {
    super(ERROR, "Writing to " + SMOOTH_DIR + " dir is forbidden.\n"
        + "Smooth keeps internal data there so don't mess with it.\n" + "Faulty path = " + path);
  }
}
