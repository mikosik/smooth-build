package org.smoothbuild.vm.evaluate.compute;

public enum ResultSource {
  DISK("cache"),
  EXECUTION("exec"),
  MEMORY("mem"),
  NOOP("")
  ;

  private final String name;

  ResultSource(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
