package org.smoothbuild.exec.compute;

public enum ResultSource {
  DISK("cache"),
  MEMORY("mem"),
  EXECUTION("exec");

  private final String name;

  ResultSource(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
