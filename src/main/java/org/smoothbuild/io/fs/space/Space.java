package org.smoothbuild.io.fs.space;

public enum Space {
  PRJ("prj"),
  SDK("sdk"),
  INTERNAL("int");

  private final String prefix;

  Space(String prefix) {
    this.prefix = prefix;
  }

  public String prefix() {
    return prefix;
  }
}