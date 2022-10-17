package org.smoothbuild.vm.compute;

public enum ResSource {
  DISK("cache"),
  EXECUTION("exec"),
  MEMORY("mem"),
  NOOP("")
  ;

  private final String name;

  ResSource(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
