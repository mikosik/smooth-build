package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.ErrorMessage;

public class FileAndFilesSpecifiedError extends ErrorMessage {
  public FileAndFilesSpecifiedError() {
    super("Parameters 'file' and 'files' cannot be provided at the same time.");
  }
}
