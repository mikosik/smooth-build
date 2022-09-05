package org.smoothbuild.compile.lang.base;

import static java.util.Objects.requireNonNull;

/**
 * Default Nal implementation.
 */
public class NalImpl extends WithLocImpl implements Nal {
  private final String name;

  public NalImpl(String name, Loc loc) {
    super(loc);
    this.name = requireNonNull(name);
  }

  @Override
  public String name() {
    return name;
  }
}
