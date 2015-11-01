package org.smoothbuild.builtin.java.javac.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.Message;

public class DuplicateClassFileError extends Message {
  public DuplicateClassFileError(Path path) {
    super(ERROR, "File " + path + " is contained by two different library jar files.");
  }
}
