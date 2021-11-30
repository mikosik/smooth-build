package org.smoothbuild.slib.java.junit;

public class JunitExc extends Exception {
  public static JunitExc brokenJunitImplementation(String message) {
    return brokenJunitImplementation(message, null);
  }

  public static JunitExc brokenJunitImplementation(String message, Throwable e) {
    return new JunitExc("JUnit implementation looks like broken: " + message, e);
  }

  public JunitExc(String message) {
    this(message, null);
  }

  public JunitExc(String message, Throwable e) {
    super(message, e);
  }
}
