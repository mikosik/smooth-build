package org.smoothbuild.compile.lang.type;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.smoothbuild.compile.lang.base.ValidNamesS;

/**
 * This class is immutable.
 */
public final class ArrayTS extends TypeS {
  private final TypeS elem;

  public ArrayTS(TypeS elem) {
    super(ValidNamesS.arrayTypeName(elem), elem.vars());
    this.elem = requireNonNull(elem);
  }

  @Override
  public TypeS mapComponents(Function<TypeS, TypeS> mapper) {
    return new ArrayTS(mapper.apply(elem));
  }

  @Override
  public ArrayTS mapVars(Function<VarS, TypeS> varMapper) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new ArrayTS(elem.mapVars(varMapper));
    }
  }

  public TypeS elem() {
    return elem;
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
