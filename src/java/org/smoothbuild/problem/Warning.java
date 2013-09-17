package org.smoothbuild.problem;

import static org.smoothbuild.problem.MessageType.WARNING;

public class Warning extends Message {
  public Warning(String message) {
    super(WARNING, message);
  }
}
