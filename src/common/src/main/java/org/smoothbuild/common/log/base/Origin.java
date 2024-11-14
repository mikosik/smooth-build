package org.smoothbuild.common.log.base;

public enum Origin {
  DISK("d-cache"),
  EXECUTION(""),
  MEMORY("m-cache"),
  ;
  private final String name;

  Origin(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
