package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;

public record MissingType(String name) implements Type {
  private static final String NAME = "Missing";

  public MissingType() {
    this(NAME);
  }

  public MissingType {
    checkArgument(NAME.equals(name));
  }

  @Override
  public String q() {
    return "'" + name + "'";
  }
}
