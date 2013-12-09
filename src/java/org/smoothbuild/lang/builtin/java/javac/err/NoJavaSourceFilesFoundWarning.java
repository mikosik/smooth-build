package org.smoothbuild.lang.builtin.java.javac.err;

import static org.smoothbuild.message.base.MessageType.WARNING;

import org.smoothbuild.message.base.Message;

public class NoJavaSourceFilesFoundWarning extends Message {
  public NoJavaSourceFilesFoundWarning() {
    super(WARNING, "Param 'sources' is empty list.");
  }
}
