package org.smoothbuild.problem;

import static org.smoothbuild.problem.MessageType.ERROR;

public class Error extends Message {
  public Error(String message) {
    super(ERROR, message);
  }
}
