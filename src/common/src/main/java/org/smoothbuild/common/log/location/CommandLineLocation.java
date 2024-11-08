package org.smoothbuild.common.log.location;

public final class CommandLineLocation implements SourceLocation {
  public static final CommandLineLocation INSTANCE = new CommandLineLocation();

  private CommandLineLocation() {}

  @Override
  public int line() {
    return 0;
  }

  @Override
  public String toString() {
    return "command line";
  }
}
