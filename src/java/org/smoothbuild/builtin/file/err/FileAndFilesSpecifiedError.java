package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.Error;

@SuppressWarnings("serial")
public class FileAndFilesSpecifiedError extends Error {
  public FileAndFilesSpecifiedError() {
    super("Parameters 'file' and 'files' cannot be provided at the same time.");
  }
}
