package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.Message;

public class IllegalReadFromSmoothDirError extends Message {
  public IllegalReadFromSmoothDirError(Path path) {
    super(ERROR, "Reading from " + SMOOTH_DIR + " dir is illegal.\n"
        + "Smooth keeps internal data there so don't mess with it.\n" + "Faulty path = " + path);
  }
}
