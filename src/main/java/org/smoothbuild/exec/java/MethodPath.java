package org.smoothbuild.exec.java;

public record MethodPath(String classBinaryName, String methodName) {
  public static MethodPath parse(String path) throws MethodPathParsingException {
    int index = path.lastIndexOf(".");
    if (index == -1 || index == 0 || index == path.length() - 1) {
      throw new MethodPathParsingException("Illegal path to java method. Expected <binary " +
          "class name>.<method name>, but was `" + path + "`.");
    } else {
      return new MethodPath(path.substring(0, index), path.substring(index + 1));
    }
  }

  @Override
  public String toString() {
    return classBinaryName + '.' + methodName;
  }

  public static class MethodPathParsingException extends Exception {
    public MethodPathParsingException(String message) {
      super(message);
    }
  }
}