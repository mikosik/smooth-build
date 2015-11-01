package org.smoothbuild.builtin.java.javac.err;

import static org.smoothbuild.lang.message.MessageType.WARNING;

import org.smoothbuild.lang.message.Message;

public class NoJavaSourceFilesFoundWarning extends Message {
  public NoJavaSourceFilesFoundWarning() {
    super(WARNING, "Param 'sources' is empty list.");
  }
}
