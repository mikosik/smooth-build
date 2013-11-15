package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.junit.runner.notification.Failure;
import org.smoothbuild.message.base.Message;

public class JunitTestFailedError extends Message {
  public JunitTestFailedError(Failure failure) {
    super(ERROR, "test failed: " + failure.toString() + "\n" + failure.getTrace());
  }
}
