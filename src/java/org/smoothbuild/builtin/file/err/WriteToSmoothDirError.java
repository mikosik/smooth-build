package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class WriteToSmoothDirError extends ErrorMessage {
  public WriteToSmoothDirError(Path path) {
    super("Writing to " + BUILD_DIR + " dir is forbidden.\n"
        + "Smooth keeps internal data there so don't mess with it.\n" + "Faulty path = " + path);
  }
}
