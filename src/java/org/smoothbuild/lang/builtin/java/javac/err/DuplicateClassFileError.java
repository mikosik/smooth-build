package org.smoothbuild.lang.builtin.java.javac.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class DuplicateClassFileError extends Message {
  public DuplicateClassFileError(Path path) {
    super(ERROR, "File " + path + " is contained by two different library jar files.");
  }
}
