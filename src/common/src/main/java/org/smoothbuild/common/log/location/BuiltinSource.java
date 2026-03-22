package org.smoothbuild.common.log.location;

public final class BuiltinSource implements CodeSource {
  public static final CodeSource INSTANCE = new BuiltinSource();

  private BuiltinSource() {}

  @Override
  public String description() {
    return "internal";
  }
}
