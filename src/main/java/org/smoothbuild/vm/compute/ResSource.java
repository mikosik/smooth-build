package org.smoothbuild.vm.compute;

public enum ResSource {
  DISK("cache"),
  MEMORY("mem"),
  EXECUTION("exec");

  private final String name;

  ResSource(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
