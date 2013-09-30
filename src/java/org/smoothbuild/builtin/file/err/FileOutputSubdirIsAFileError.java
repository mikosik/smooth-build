package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class FileOutputSubdirIsAFileError extends ErrorMessage {
  public FileOutputSubdirIsAFileError(Path dirPath, Path filePath, Path path) {
    super("Cannot save " + filePath + " to dir " + dirPath + " as " + path + " is a file.");
  }
}
