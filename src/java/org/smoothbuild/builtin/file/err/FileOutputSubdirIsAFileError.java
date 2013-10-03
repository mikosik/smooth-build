package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageType;
import org.smoothbuild.type.api.Path;

public class FileOutputSubdirIsAFileError extends Message {
  public FileOutputSubdirIsAFileError(Path dirPath, Path filePath, Path path) {
    super(MessageType.ERROR, "Cannot save " + filePath + " to dir " + dirPath + " as " + path
        + " is a file.");
  }
}
