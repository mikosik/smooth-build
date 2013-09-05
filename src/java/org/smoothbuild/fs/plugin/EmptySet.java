package org.smoothbuild.fs.plugin;

public class EmptySet {
  private static final EmptySet INSTANCE = new EmptySet();

  private EmptySet() {}

  public static EmptySet emptySet() {
    return INSTANCE;
  }
}
