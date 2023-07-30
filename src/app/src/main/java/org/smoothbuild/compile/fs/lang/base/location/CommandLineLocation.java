package org.smoothbuild.compile.fs.lang.base.location;

import static org.smoothbuild.fs.space.Space.PROJECT;

import org.smoothbuild.fs.space.Space;

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
