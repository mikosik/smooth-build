package org.smoothbuild.builtin.java.javac.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;

public class DuplicateClassFileError extends Message {
  public DuplicateClassFileError(Path path, Path jar1, Path jar2) {
    super(ERROR, "File " + path + " is contained by both library jar files: " + jar1 + " and "
        + jar2 + ".");
  }
}
