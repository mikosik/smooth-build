package org.smoothbuild.lang.base.define;

public enum Space {
  USER("prj"),
  STANDARD_LIBRARY("slib"),
  INTERNAL("int");

  private final String prefix;

  Space(String prefix) {
    this.prefix = prefix;
  }

  public String prefix() {
    return prefix;
  }
}
