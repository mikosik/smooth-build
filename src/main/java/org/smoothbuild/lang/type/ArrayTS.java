package org.smoothbuild.lang.type;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class ArrayTS extends MonoTS implements ComposedTS {
  private final MonoTS elem;

  public ArrayTS(MonoTS elem) {
    super(TNamesS.arrayTypeName(elem), elem.vars());
    this.elem = requireNonNull(elem);
  }

  @Override
  public boolean includes(MonoTS type) {
    return this.equals(type) || elem.includes(type);
  }

  @Override
  public MonoTS mapVars(Function<VarS, VarS> varMapper) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new ArrayTS(elem.mapVars(varMapper));
    }
  }

  public MonoTS elem() {
    return elem;
  }

  @Override
  public ImmutableList<MonoTS> covars() {
    return ImmutableList.of(elem);
  }

  @Override
  public ImmutableList<MonoTS> contravars() {
    return ImmutableList.of();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ArrayTS thatArray
        && this.elem().equals(thatArray.elem());
  }
}
