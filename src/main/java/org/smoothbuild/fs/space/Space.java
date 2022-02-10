package org.smoothbuild.fs.space;

public enum Space {
  PRJ("prj"),
  SLIB("slib"),
  INTERNAL("int"),
  UNKNOWN("unk");

  private final String prefix;

  Space(String prefix) {
    this.prefix = prefix;
  }

  public String prefix() {
    return prefix;
  }
}
