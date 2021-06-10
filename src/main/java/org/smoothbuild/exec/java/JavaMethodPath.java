package org.smoothbuild.exec.java;

public record JavaMethodPath(String classBinaryName, String methodName) {
  public static JavaMethodPath parse(String path) throws JavaMethodPathParsingException {
    int index = path.lastIndexOf(".");
    if (index == -1 || index == 0 || index == path.length() - 1) {
      throw new JavaMethodPathParsingException("Illegal path to java method. Expected <binary " +
          "class name>.<method name>, but was `" + path + "`.");
    } else {
      return new JavaMethodPath(path.substring(0, index), path.substring(index + 1));
    }
  }

  @Override
  public String toString() {
    return classBinaryName + '.' + methodName;
  }

  public static class JavaMethodPathParsingException extends Exception {
    public JavaMethodPathParsingException(String message) {
      super(message);
    }
  }
}
