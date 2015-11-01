package org.smoothbuild.builtin.java.junit.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.junit.runner.notification.Failure;
import org.smoothbuild.lang.message.Message;

public class JunitTestFailedError extends Message {
  public JunitTestFailedError(Failure failure) {
    super(ERROR, "test failed: " + failure.toString() + "\n" + failure.getTrace());
  }
}
