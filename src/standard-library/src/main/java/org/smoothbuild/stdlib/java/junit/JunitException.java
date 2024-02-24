package org.smoothbuild.stdlib.java.junit;

public class JunitException extends Exception {
  public static JunitException brokenJunitImplementation(String message) {
    return brokenJunitImplementation(message, null);
  }

  public static JunitException brokenJunitImplementation(String message, Throwable e) {
    return new JunitException("JUnit implementation looks like broken: " + message, e);
  }

  public JunitException(String message) {
    this(message, null);
  }

  public JunitException(String message, Throwable e) {
    super(message, e);
  }
}
