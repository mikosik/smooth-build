package org.smoothbuild.builtin.compress;

@SuppressWarnings("serial")
public class IllegalPathInZipException extends Exception {
  private final String fileName;

  public IllegalPathInZipException(String fileName) {
    this.fileName = fileName;
  }

  public String fileName() {
    return fileName;
  }
}
