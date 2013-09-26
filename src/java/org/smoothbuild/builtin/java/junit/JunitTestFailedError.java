package org.smoothbuild.builtin.java.junit;

import org.junit.runner.notification.Failure;
import org.smoothbuild.message.Error;

@SuppressWarnings("serial")
public class JunitTestFailedError extends Error {
  public JunitTestFailedError(Failure failure) {
    super("test failed: " + failure.toString() + "\n" + failure.getTrace());
  }
}
