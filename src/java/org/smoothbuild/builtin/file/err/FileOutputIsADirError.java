package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.api.Path;

public class FileOutputIsADirError extends Message {
  public FileOutputIsADirError(Path dirPath, Path filePath) {
    super(ERROR, "Cannot save " + filePath + " to dir " + dirPath + " as "
        + dirPath.append(filePath) + " is a directory.");
  }
}
