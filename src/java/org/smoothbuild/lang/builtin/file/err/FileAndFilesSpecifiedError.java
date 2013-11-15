package org.smoothbuild.lang.builtin.file.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

public class FileAndFilesSpecifiedError extends Message {
  public FileAndFilesSpecifiedError() {
    super(ERROR, "Parameters 'file' and 'files' cannot be provided at the same time.");
  }
}
