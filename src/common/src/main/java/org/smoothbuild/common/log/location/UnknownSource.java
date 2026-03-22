package org.smoothbuild.common.log.location;

public final class UnknownSource implements CodeSource {
  public static final UnknownSource INSTANCE = new UnknownSource();

  private UnknownSource() {}

  @Override
  public String description() {
    return "???";
  }
}
