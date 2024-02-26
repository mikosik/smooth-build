package org.smoothbuild.compile.frontend.lang.base.location;

import static org.smoothbuild.filesystem.space.SmoothSpace.PROJECT;

import org.smoothbuild.common.filesystem.space.Space;

public final class CommandLineLocation implements SourceLocation {
  public static final CommandLineLocation INSTANCE = new CommandLineLocation();

  private CommandLineLocation() {}

  @Override
  public int line() {
    return 0;
  }

  @Override
  public Space space() {
    return PROJECT;
  }

  @Override
  public String toString() {
    return "command line";
  }
}
