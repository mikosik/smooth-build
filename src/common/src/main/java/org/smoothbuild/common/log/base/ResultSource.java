package org.smoothbuild.common.log.base;

public enum ResultSource {
  DISK("d-cache"),
  EXECUTION(""),
  MEMORY("m-cache"),
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
