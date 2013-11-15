package org.smoothbuild.lang.builtin.file.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class DuplicateMergedPathError extends Message {
  public DuplicateMergedPathError(Path path) {
    super(ERROR, "Both parameters ('files' and 'with') contain file with path = " + path + ".");
  }
}
