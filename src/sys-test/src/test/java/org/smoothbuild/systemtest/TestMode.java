package org.smoothbuild.systemtest;

public enum TestMode {
  SINGLE_JVM,
  FULL_BINARY;

  private static final String SYSTEM_PROPERTY = "fast.system.tests";

  public static TestMode detectTestMode() {
    var mode = System.getProperty(SYSTEM_PROPERTY);
    return switch (mode) {
      case "true", "" -> SINGLE_JVM;
      case "false" -> FULL_BINARY;
      default -> throw new RuntimeException(
          "System property " + SYSTEM_PROPERTY + " has illegal value `" + mode + "`");
    };
  }
}
