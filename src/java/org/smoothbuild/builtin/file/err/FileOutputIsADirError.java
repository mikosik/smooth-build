package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class FileOutputIsADirError extends ErrorMessage {
  public FileOutputIsADirError(Path dirPath, Path filePath) {
    super("Cannot save " + filePath + " to dir " + dirPath + " as " + dirPath.append(filePath)
        + " is a directory.");
  }
}
