package org.smoothbuild.lang.type;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class ArrayTS extends TypeS implements ComposedTS {
  private final TypeS elem;

  public ArrayTS(TypeS elem) {
    super(TNamesS.arrayTypeName(elem), elem.vars());
    this.elem = requireNonNull(elem);
  }

  @Override
  public ArrayTS withPrefixedVars(String prefix) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new ArrayTS(elem.withPrefixedVars(prefix));
    }
  }

  public TypeS elem() {
    return elem;
  }

  @Override
  public ImmutableList<TypeS> covars() {
    return ImmutableList.of(elem);
  }

  @Override
  public ImmutableList<TypeS> contravars() {
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
