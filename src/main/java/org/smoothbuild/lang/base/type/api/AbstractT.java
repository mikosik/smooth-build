package org.smoothbuild.lang.base.type.api;

import static com.google.common.base.Preconditions.checkArgument;

public non-sealed abstract class AbstractT implements Type {
  protected final String name;
  private final boolean hasOpenVars;
  private final boolean hasClosedVars;

  public AbstractT(String name, boolean hasOpenVars, boolean hasClosedVars) {
    checkArgument(!name.isBlank());
    this.hasOpenVars = hasOpenVars;
    this.hasClosedVars = hasClosedVars;
    this.name = name;

  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public boolean hasOpenVars() {
    return hasOpenVars;
  }

  @Override
  public boolean hasClosedVars() {
    return hasClosedVars;
  }
}
