package org.smoothbuild.lang.define;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.smoothbuild.lang.type.PolyFuncTS;

public final class PolyFuncS extends FuncS implements PolyTopRefableS {
  private final PolyFuncTS type;
  private final MonoFuncS func;

  public PolyFuncS(PolyFuncTS type, MonoFuncS func) {
    super(func.modPath(), func.name(), func.params(), func.loc());
    this.type = requireNonNull(type);
    this.func = requireNonNull(func);
  }

  public static PolyFuncS polyFuncS(MonoFuncS funcS) {
    var type = funcS.type();
    return new PolyFuncS(new PolyFuncTS(type.vars(), type), funcS);
  }

  @Override
  public PolyFuncTS type() {
    return type;
  }

  public MonoFuncS func() {
    return func;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PolyFuncS polyFuncS
        && type.equals(polyFuncS.type)
        && func.equals(polyFuncS.func);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, func);
  }

  @Override
  public String toString() {
    return type.freeVars() + func.toString();
  }
}
