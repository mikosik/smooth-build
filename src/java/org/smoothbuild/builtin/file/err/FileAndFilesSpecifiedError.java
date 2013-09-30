package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class FileAndFilesSpecifiedError extends Message {
  public FileAndFilesSpecifiedError() {
    super(ERROR, "Parameters 'file' and 'files' cannot be provided at the same time.");
  }
}
