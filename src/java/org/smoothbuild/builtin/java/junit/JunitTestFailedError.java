package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.junit.runner.notification.Failure;
import org.smoothbuild.message.message.Message;

public class JunitTestFailedError extends Message {
  public JunitTestFailedError(Failure failure) {
    super(ERROR, "test failed: " + failure.toString() + "\n" + failure.getTrace());
  }
}
