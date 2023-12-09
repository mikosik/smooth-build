package org.smoothbuild.filesystem.space;

public enum Space {
  PROJECT("prj"),
  STANDARD_LIBRARY("ssl"),
  BINARY("bin"),
  ;

  private final String prefix;

  Space(String prefix) {
    this.prefix = prefix;
  }

  public String prefix() {
    return prefix;
  }
}
