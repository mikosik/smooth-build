package org.smoothbuild.layout;

import org.smoothbuild.common.filesystem.base.Space;

public enum SmoothSpace implements Space {
  PROJECT("prj"),
  STANDARD_LIBRARY("ssl"),
  BINARY("bin"),
  ;

  private final String prefix;

  SmoothSpace(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public String prefix() {
    return prefix;
  }
}
