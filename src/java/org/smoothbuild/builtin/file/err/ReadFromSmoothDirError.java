package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.api.Path;

public class ReadFromSmoothDirError extends Message {
  public ReadFromSmoothDirError(Path path) {
    super(ERROR, "Reading from " + BUILD_DIR + " dir is forbidden.\n"
        + "Smooth keeps internal data there so don't mess with it.\n" + "Faulty path = " + path);
  }
}
