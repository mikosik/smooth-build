package org.smoothbuild.common.log.base;

public enum ResultSource {
  DISK("cache"),
  EXECUTION(""),
  MEMORY("mem"),
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
