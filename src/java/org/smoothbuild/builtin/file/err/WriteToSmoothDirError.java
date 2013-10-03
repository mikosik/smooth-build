package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.type.api.Path;

public class WriteToSmoothDirError extends Message {
  public WriteToSmoothDirError(Path path) {
    super(ERROR, "Writing to " + BUILD_DIR + " dir is forbidden.\n"
        + "Smooth keeps internal data there so don't mess with it.\n" + "Faulty path = " + path);
  }
}
