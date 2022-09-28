package org.smoothbuild.compile.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;

import java.util.Objects;
import java.util.function.Function;

import org.smoothbuild.util.collect.Named;

/**
 * Monomorphic type.
 * This class and all its subclasses are immutable.
 */
public abstract sealed class TypeS implements Named
    permits ArrayTS, BaseTS, FuncTS, StructTS, TupleTS, VarS {
  private final VarSetS vars;
  private final String name;

  protected TypeS(String name) {
    this(name, varSetS());
  }

  protected TypeS(String name, VarSetS vars) {
    checkArgument(!name.isBlank());
    this.name = name;
    this.vars = vars;
  }

  @Override
  public String name() {
    return name;
  }

  public VarSetS vars() {
    return vars;
  }

  public TypeS mapComponents(Function<TypeS, TypeS> mapper) {
    return this;
  }

  public TypeS mapVars(Function<VarS, TypeS> varMapper) {
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof TypeS that
        && getClass().equals(object.getClass())
        && this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }

  @Override
  public String toString() {
    return name();
  }
}
