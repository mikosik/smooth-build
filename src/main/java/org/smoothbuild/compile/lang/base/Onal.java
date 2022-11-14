package org.smoothbuild.compile.lang.base;

import java.util.Optional;

import org.smoothbuild.util.collect.Nameable;

/**
 * Onal = Optional name + Loc
 */
public class Onal extends WithLocImpl implements Nameable {
  private final Optional<String> name;

  public Onal(Optional<String> name, Loc loc) {
    super(loc);
    this.name = name;
  }

  @Override
  public Optional<String> nameO() {
    return name;
  }
}
