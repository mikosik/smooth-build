package org.smoothbuild.lang.base.type.impl;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.api.ArrayT;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeNames;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class ArrayTS extends TypeS implements ArrayT {
  private final TypeS elem;

  public ArrayTS(TypeS elem) {
    super(TypeNames.arrayTypeName(elem), elem.openVars(), elem.hasClosedVars());
    this.elem = requireNonNull(elem);
  }

  @Override
  public TypeS elem() {
    return elem;
  }

  @Override
  public ImmutableList<Type> covars() {
    return ImmutableList.of(elem);
  }

  @Override
  public ImmutableList<Type> contravars() {
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
