package org.smoothbuild.builtin.android;

public class EnvironmentVariable {
  private final String name;
  private final String value;
  private final boolean isSet;

  public static EnvironmentVariable environmentVariable(String variableName) {
    return new EnvironmentVariable(variableName, System.getenv(variableName));
  }

  protected EnvironmentVariable(String name, String value) {
    this.name = name;
    this.value = value;
    this.isSet = value != null;
  }

  public String name() {
    return name;
  }

  public String value() {
    if (!isSet) {
      throw new IllegalStateException(
          "Cannot call EnvironmentVariable.value() when isSet() == false");
    }
    return value;
  }

  public boolean isSet() {
    return isSet;
  }

  public String toString() {
    return name + "=" + value;
  }
}
