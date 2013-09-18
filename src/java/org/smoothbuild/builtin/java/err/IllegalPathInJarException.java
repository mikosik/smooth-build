package org.smoothbuild.builtin.java.err;

@SuppressWarnings("serial")
public class IllegalPathInJarException extends Exception {
  private final String fileName;

  public IllegalPathInJarException(String fileName) {
    this.fileName = fileName;
  }

  public String fileName() {
    return fileName;
  }
}
