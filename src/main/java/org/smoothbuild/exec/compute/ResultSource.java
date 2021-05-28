package org.smoothbuild.exec.compute;

public enum ResultSource {
  CONST("const"),
  DISK("cache"),
  MEMORY("mem"),
  EXECUTION(""),
  GROUP("group");

  private final String name;

  ResultSource(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
