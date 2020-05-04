package org.smoothbuild.exec.task.base;

public enum ResultSource {
  CONST("const"),
  CACHE("cache"),
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
