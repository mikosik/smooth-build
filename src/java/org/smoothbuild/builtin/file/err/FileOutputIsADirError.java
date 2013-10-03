package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;

public class FileOutputIsADirError extends Message {
  public FileOutputIsADirError(Path dirPath, Path filePath) {
    super(ERROR, "Cannot save " + filePath + " to dir " + dirPath + " as "
        + dirPath.append(filePath) + " is a directory.");
  }
}
