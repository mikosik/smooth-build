package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;

import org.smoothbuild.message.Error;
import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class ReadFromSmoothDirError extends Error {
  public ReadFromSmoothDirError(Path path) {
    super("Reading from " + BUILD_DIR + " dir is forbidden.\n"
        + "Smooth keeps internal data there so don't mess with it.\n" + "Faulty path = " + path);
  }
}
