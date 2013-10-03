package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.type.api.Path;

public class DuplicateMergedPathError extends Message {
  public DuplicateMergedPathError(Path path) {
    super(ERROR, "Both parameters ('files' and 'with') contain file with path = " + path + ".");
  }
}
