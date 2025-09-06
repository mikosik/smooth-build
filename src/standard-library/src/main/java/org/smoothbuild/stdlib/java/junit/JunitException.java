package org.smoothbuild.stdlib.java.junit;

public class JunitException extends Exception {
  public static JunitException brokenJunitImplementation(String message) {
    return new JunitException(message);
  }

  public static JunitException brokenJunitImplementation(String message, Throwable e) {
    return new JunitException(message(message), e);
  }

  private static String message(String message) {
    return "JUnit implementation looks like broken: " + message;
  }

  public JunitException(String message) {
    super(message);
  }

  public JunitException(String message, Throwable e) {
    super(message, e);
  }
}
