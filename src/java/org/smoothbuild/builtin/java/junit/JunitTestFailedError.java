package org.smoothbuild.builtin.java.junit;

import org.junit.runner.notification.Failure;
import org.smoothbuild.message.message.ErrorMessage;

public class JunitTestFailedError extends ErrorMessage {
  public JunitTestFailedError(Failure failure) {
    super("test failed: " + failure.toString() + "\n" + failure.getTrace());
  }
}
