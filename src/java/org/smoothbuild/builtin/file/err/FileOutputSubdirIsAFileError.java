package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.listen.MessageType;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.api.Path;

public class FileOutputSubdirIsAFileError extends Message {
  public FileOutputSubdirIsAFileError(Path dirPath, Path filePath, Path path) {
    super(MessageType.ERROR, "Cannot save " + filePath + " to dir " + dirPath + " as " + path
        + " is a file.");
  }
}
