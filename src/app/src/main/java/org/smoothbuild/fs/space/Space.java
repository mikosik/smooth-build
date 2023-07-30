package org.smoothbuild.fs.space;

public enum Space {
  PRJ("prj"),
  STD_LIB("std-lib"),
  BIN("bin"),
  ;

  private final String prefix;

  Space(String prefix) {
    this.prefix = prefix;
  }

  public String prefix() {
    return prefix;
  }
}
