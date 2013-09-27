package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.Error;
import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class FileOutputIsADirError extends Error {
  public FileOutputIsADirError(Path dirPath, Path filePath) {
    super("Cannot save " + filePath + " to dir " + dirPath + " as " + dirPath.append(filePath)
        + " is a directory.");
  }
}
