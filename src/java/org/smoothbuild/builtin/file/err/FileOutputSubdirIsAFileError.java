package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.Error;
import org.smoothbuild.plugin.api.Path;

public class FileOutputSubdirIsAFileError extends Error {
  public FileOutputSubdirIsAFileError(Path dirPath, Path filePath, Path path) {
    super("Cannot save " + filePath + " to dir " + dirPath + " as " + path + " is a file.");
  }
}
