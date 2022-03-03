package org.smoothbuild.lang.type.api;

import static com.google.common.base.Preconditions.checkArgument;

public non-sealed abstract class AbstractT implements Type {
  protected final String name;

  public AbstractT(String name) {
    checkArgument(!name.isBlank());
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }
}
