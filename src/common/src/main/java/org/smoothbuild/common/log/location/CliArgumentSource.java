package org.smoothbuild.common.log.location;

public final class CliArgumentSource implements CodeSource {
  public static final CodeSource INSTANCE = new CliArgumentSource();

  private CliArgumentSource() {}

  @Override
  public String description() {
    return "command line";
  }
}
