package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;

public class DuplicateMergedPathError extends Message {
  public DuplicateMergedPathError(Path path) {
    super(ERROR, "Both parameters ('files' and 'with') contain file with path = " + path + ".");
  }
}
