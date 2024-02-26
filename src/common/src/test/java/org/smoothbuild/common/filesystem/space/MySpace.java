package org.smoothbuild.common.filesystem.space;

record MySpace(String name) implements Space {
  @Override
  public String prefix() {
    return name;
  }
}
